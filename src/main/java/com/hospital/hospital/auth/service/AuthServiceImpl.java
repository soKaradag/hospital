package com.hospital.hospital.auth.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.auth.dto.AuthTokenResponse;
import com.hospital.hospital.auth.dto.CurrentUserResponse;
import com.hospital.hospital.auth.dto.LoginRequest;
import com.hospital.hospital.auth.dto.RefreshTokenRequest;
import com.hospital.hospital.auth.model.RefreshToken;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserInfo;
import com.hospital.hospital.auth.repository.RefreshTokenRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.token.AuthTokenPair;
import com.hospital.hospital.auth.token.InvalidTokenException;
import com.hospital.hospital.auth.token.JwtTokenService;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.common.exception.UnauthorizedException;

/*
- Auth iş akışlarının uygulama katmanı burada yürütülür.
- Login, refresh, logout ve me işlemleri controller'dan ayrılarak burada toplanır.
- Bu adımda henüz merkezi auth interceptor olmadığı için bearer token çözümleme burada yapılır.
- Refresh token DB üzerinden yönetilir; bu sayede revoke ve rotation davranışı uygulanabilir.
*/
@Service
public class AuthServiceImpl implements AuthService {

	private static final String BEARER_PREFIX = "Bearer ";

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenService jwtTokenService;
	private final PasswordHashService passwordHashService;

	public AuthServiceImpl(
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			JwtTokenService jwtTokenService,
			PasswordHashService passwordHashService) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtTokenService = jwtTokenService;
		this.passwordHashService = passwordHashService;
	}

	@Override
	@Transactional
	// Login akışı kullanıcıyı bulur, parolayı doğrular, token çifti üretir ve refresh token kaydını kalıcılaştırır.
	public AuthTokenResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

		if (!passwordHashService.matches(request.getPassword(), user.getPasswordHash())) {
			throw new UnauthorizedException("Invalid username or password");
		}

		AuthTokenPair tokenPair = jwtTokenService.generateTokenPair(user);
		saveRefreshToken(user, tokenPair.refreshToken());

		return new AuthTokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
	}

	@Override
	@Transactional
	// Refresh akışı eski refresh token'ı doğrular, revoke eder ve token rotation ile yeni çift üretir.
	// Rotation tercih edilmesinin nedeni, çalınmış eski refresh tokenların tekrar kullanılmasını zorlaştırmaktır.
	public AuthTokenResponse refresh(RefreshTokenRequest request) {
		RefreshToken refreshToken = getActiveRefreshToken(request.getRefreshToken());
		TokenPrincipal principal = parseRefreshToken(request.getRefreshToken());

		if (!refreshToken.getUser().getId().equals(principal.userId())) {
			throw new UnauthorizedException("Refresh token is invalid");
		}

		refreshToken.setRevokedAt(Instant.now());
		User user = refreshToken.getUser();
		AuthTokenPair tokenPair = jwtTokenService.generateTokenPair(user);
		saveRefreshToken(user, tokenPair.refreshToken());

		return new AuthTokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
	}

	@Override
	@Transactional
	// Logout işlemi refresh token kaydını iptal eder; böylece yeni access token üretimi engellenir.
	public void logout(RefreshTokenRequest request) {
		RefreshToken refreshToken = getActiveRefreshToken(request.getRefreshToken());
		parseRefreshToken(request.getRefreshToken());
		refreshToken.setRevokedAt(Instant.now());
	}

	@Override
	@Transactional(readOnly = true)
	// Me akışı access token'dan kullanıcı kimliğini çözer ve güncel kullanıcı bilgisini veritabanından döner.
	// Veritabanından tekrar okuma yapılmasının nedeni, token içeriğine körü körüne güvenmek yerine güncel kullanıcı kaydını kullanmaktır.
	public CurrentUserResponse me(String authorizationHeader) {
		TokenPrincipal principal = parseAccessToken(extractBearerToken(authorizationHeader));
		User user = userRepository.findById(principal.userId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.userId()));

		UserInfo userInfo = user.getUserInfo();
		return new CurrentUserResponse(
				user.getId(),
				user.getUsername(),
				user.getRole(),
				userInfo != null ? userInfo.getFirstName() : null,
				userInfo != null ? userInfo.getLastName() : null,
				userInfo != null && userInfo.getContact() != null ? userInfo.getContact().getEmail() : null);
	}

	// Yeni üretilen refresh token'ın ham değeri istemciye dönerken, veritabanında yalnızca hash değeri saklanır.
	// ExpiresAt bilgisi token içinden tekrar okunur; böylece DB kaydı ile JWT süresi aynı kaynaktan beslenir.
	private void saveRefreshToken(User user, String rawRefreshToken) {
		TokenPrincipal principal = parseRefreshToken(rawRefreshToken);
		RefreshToken refreshToken = new RefreshToken(
				user,
				jwtTokenService.hashToken(rawRefreshToken),
				principal.expiresAt());
		refreshTokenRepository.save(refreshToken);
	}

	// Refresh token kaydını veritabanından bulur ve aktiflik kontrollerini yapar.
	// Revoked veya süresi dolmuş tokenların burada erken reddedilmesi, refresh/logout akışlarını sadeleştirir.
	private RefreshToken getActiveRefreshToken(String rawRefreshToken) {
		String tokenHash = jwtTokenService.hashToken(rawRefreshToken);
		RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
				.orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

		if (refreshToken.getRevokedAt() != null) {
			throw new UnauthorizedException("Refresh token is revoked");
		}

		if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
			throw new UnauthorizedException("Refresh token is expired");
		}

		return refreshToken;
	}

	// JWT servisinden gelen teknik token hatalarını auth katmanının ortak unauthorized hatasına dönüştürür.
	private TokenPrincipal parseRefreshToken(String token) {
		try {
			return jwtTokenService.parseRefreshToken(token);
		} catch (InvalidTokenException exception) {
			throw new UnauthorizedException(exception.getMessage());
		}
	}

	// Access token parse akışı da aynı şekilde istemciye güvenli ve tutarlı bir hata döndürmek için sarılır.
	private TokenPrincipal parseAccessToken(String token) {
		try {
			return jwtTokenService.parseAccessToken(token);
		} catch (InvalidTokenException exception) {
			throw new UnauthorizedException(exception.getMessage());
		}
	}

	// Authorization header içinden Bearer token'ı çıkarır.
	// Bu yardımcı metot controller'ı sade tutar; sonraki adımda merkezi interceptor geldiğinde bu kod kolayca taşınabilir.
	private String extractBearerToken(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new UnauthorizedException("Authorization header is required");
		}

		if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
			throw new UnauthorizedException("Authorization header must use Bearer token");
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
		if (token.isBlank()) {
			throw new UnauthorizedException("Bearer token must not be blank");
		}
		return token;
	}
}
