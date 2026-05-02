package com.hospital.hospital.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.model.User;

/*
 * JWT token üretimi ve doğrulama işlemlerini yöneten servistir.
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

	// JWT standardında base64url kullanılır; withoutPadding çıktıyı daha kısa ve standart hale getirir.
	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
	private static final String HMAC_SHA256 = "HmacSHA256";
	// Payload decode edildiğinde claim değerlerini esnek şekilde okuyabilmek için Map'e dönüştürülür.
	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
	};

	// Spring bean'ine bağlı kalmadan çalışan sade bir JSON mapper kullanılır.
	// findAndRegisterModules, tarih/saat gibi tiplerde ek modüllerin otomatik yüklenmesini sağlar.
	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
	private final byte[] secretKey;
	private final long accessTokenExpirationSeconds;
	private final long refreshTokenExpirationSeconds;

	// Constructor, token üretim ve doğrulama için gerekli sabit ayarları yükler.
	// Secret değerini burada doğrulamak, hatalı konfigürasyonun uygulama açılırken erken fark edilmesini sağlar.
	public JwtTokenServiceImpl(
			@Value("${auth.jwt.secret}") String secret,
			@Value("${auth.jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
			@Value("${auth.jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds) {
		this.secretKey = validateSecret(secret);
		this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
		this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
	}

	@Override
	// Access token üretimini dışarı sade bir metotla açar; gerçek üretim detayını ortak metoda bırakır.
	public String generateAccessToken(User user) {
		return generateToken(user, TokenType.ACCESS, accessTokenExpirationSeconds);
	}

	@Override
	// Refresh token üretimini access token ile aynı altyapı üzerinden yapar.
	// Bu tekrar eden JWT kurulum kodunu tek yerde toplamak için tercih edilir.
	public String generateRefreshToken(User user) {
		return generateToken(user, TokenType.REFRESH, refreshTokenExpirationSeconds);
	}

	@Override
	// Login ve refresh response'larında çoğunlukla iki token birlikte döneceği için yardımcı bir çift üretim metodu sunar.
	public AuthTokenPair generateTokenPair(User user) {
		return new AuthTokenPair(generateAccessToken(user), generateRefreshToken(user));
	}

	@Override
	// Access token parse edilirken beklenen token tipini açıkça ACCESS olarak sabitler.
	public TokenPrincipal parseAccessToken(String token) {
		return parse(token, TokenType.ACCESS);
	}

	@Override
	// Refresh token parse edilirken yanlış token tiplerinin kabul edilmesini engeller.
	public TokenPrincipal parseRefreshToken(String token) {
		return parse(token, TokenType.REFRESH);
	}

	@Override
	// Refresh token veritabanında düz metin yerine hash olarak saklanır.
	// SHA-256 burada tek yönlü ve hızlı bir karşılaştırma anahtarı üretmek için kullanılır.
	public String hashToken(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			return toHex(hashed);
		} catch (Exception ex) {
			throw new IllegalStateException("Token hash could not be generated", ex);
		}
	}

	// JWT üretiminin gerçek gövdesi burada yer alır.
	// Access ve refresh token için tek kod yolu kullanmak, claim yapısının tutarlı kalmasını sağlar.
	// LinkedHashMap seçimi, payload alanlarının yazım sırasını sabit tutarak debug etmeyi kolaylaştırır.
	private String generateToken(User user, TokenType tokenType, long expirationSeconds) {
		if (user.getId() == null) {
			throw new IllegalArgumentException("User id is required for token generation");
		}

		Instant now = Instant.now();
		Instant expiresAt = now.plusSeconds(expirationSeconds);

		Map<String, Object> header = Map.of(
				"alg", "HS256",
				"typ", "JWT");

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("sub", user.getId().toString());
		payload.put("username", user.getUsername());
		payload.put("role", user.getRole().name());
		payload.put("token_type", tokenType.name());
		payload.put("jti", UUID.randomUUID().toString());
		payload.put("iat", now.getEpochSecond());
		payload.put("exp", expiresAt.getEpochSecond());

		String encodedHeader = encodeJson(header);
		String encodedPayload = encodeJson(payload);
		String unsignedToken = encodedHeader + "." + encodedPayload;

		return unsignedToken + "." + sign(unsignedToken);
	}

	// Gelen JWT burada yapısal olarak doğrulanır ve claim'leri okunur.
	// Format, imza, tip ve süre kontrolleri tek yerde tutulur; böylece tüm parse akışları aynı güvenlik adımlarından geçer.
	// MessageDigest.isEqual kullanımı, imza karşılaştırmasını daha güvenli hale getirmek için tercih edilir.
	private TokenPrincipal parse(String token, TokenType expectedType) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			throw new InvalidTokenException("Token format is invalid");
		}

		String unsignedToken = parts[0] + "." + parts[1];
		String expectedSignature = sign(unsignedToken);
		if (!MessageDigest.isEqual(
				expectedSignature.getBytes(StandardCharsets.UTF_8),
				parts[2].getBytes(StandardCharsets.UTF_8))) {
			throw new InvalidTokenException("Token signature is invalid");
		}

		Map<String, Object> payload = decodePayload(parts[1]);

		TokenType tokenType = TokenType.valueOf(readStringClaim(payload, "token_type"));
		if (tokenType != expectedType) {
			throw new InvalidTokenException("Token type is invalid");
		}

		Instant expiresAt = Instant.ofEpochSecond(readLongClaim(payload, "exp"));
		if (expiresAt.isBefore(Instant.now())) {
			throw new InvalidTokenException("Token is expired");
		}

		return new TokenPrincipal(
				UUID.fromString(readStringClaim(payload, "sub")),
				readStringClaim(payload, "username"),
				Role.valueOf(readStringClaim(payload, "role")),
				tokenType,
				Instant.ofEpochSecond(readLongClaim(payload, "iat")),
				expiresAt);
	}

	// JWT'nin payload kısmını base64url'den çözüp claim map'ine dönüştürür.
	// Ayrı metot tutulmasının nedeni parse akışını sadeleştirmek ve decode hatalarını tek noktada toplamak.
	private Map<String, Object> decodePayload(String encodedPayload) {
		try {
			byte[] decodedPayload = BASE64_URL_DECODER.decode(encodedPayload);
			return objectMapper.readValue(decodedPayload, MAP_TYPE);
		} catch (Exception ex) {
			throw new InvalidTokenException("Token payload is invalid");
		}
	}

	// Header ve payload verisini JSON'a çevirip JWT standardına uygun base64url formata dönüştürür.
	private String encodeJson(Map<String, Object> data) {
		try {
			byte[] jsonBytes = objectMapper.writeValueAsBytes(data);
			return BASE64_URL_ENCODER.encodeToString(jsonBytes);
		} catch (JsonProcessingException ex) {
			throw new IllegalStateException("Token payload could not be serialized", ex);
		}
	}

	// JWT imzasını HMAC SHA-256 ile üretir.
	// Ayrı bir metot olarak tutulması hem üretim hem doğrulama tarafında aynı imza mantığını paylaşmamızı sağlar.
	private String sign(String value) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA256);
			mac.init(new SecretKeySpec(secretKey, HMAC_SHA256));
			byte[] signatureBytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
			return BASE64_URL_ENCODER.encodeToString(signatureBytes);
		} catch (Exception ex) {
			throw new IllegalStateException("Token signature could not be generated", ex);
		}
	}

	// String claim okumalarında tip ve boş değer kontrolünü tek noktada toplar.
	// Bu sayede claim doğrulama kodu parse metodunda tekrar etmez.
	private String readStringClaim(Map<String, Object> payload, String claimName) {
		Object value = payload.get(claimName);
		if (!(value instanceof String stringValue) || stringValue.isBlank()) {
			throw new InvalidTokenException("Token claim is invalid: " + claimName);
		}
		return stringValue;
	}

	// Sayısal claim okumalarında hem doğrudan Number hem de JsonNode desteği verilir.
	// Bunun nedeni JSON parse sonuçlarının kullanılan mapper ve veri tipine göre farklı temsil edilebilmesidir.
	private long readLongClaim(Map<String, Object> payload, String claimName) {
		Object value = payload.get(claimName);
		if (value instanceof Number numberValue) {
			return numberValue.longValue();
		}
		if (value instanceof JsonNode nodeValue && nodeValue.isNumber()) {
			return nodeValue.longValue();
		}
		throw new InvalidTokenException("Token claim is invalid: " + claimName);
	}

	// Secret değerinin boş olmaması ve minimum güvenli uzunlukta olması burada garanti edilir.
	// 32 byte altındaki anahtarlar HMAC için zayıf kabul edileceği için uygulama erken durdurulur.
	private byte[] validateSecret(String secret) {
		if (secret == null || secret.isBlank()) {
			throw new IllegalStateException("auth.jwt.secret must not be blank");
		}

		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (secretBytes.length < 32) {
			throw new IllegalStateException("auth.jwt.secret must be at least 32 bytes");
		}

		return secretBytes;
	}

	// Hash sonucunu veritabanında taşımaya uygun sabit uzunlukta hex string'e çevirir.
	private String toHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) {
			builder.append(String.format("%02x", value));
		}
		return builder.toString();
	}
}
