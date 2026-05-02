package com.hospital.inventory.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

	private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final String HMAC_SHA256 = "HmacSHA256";
	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
	};

	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
	private final byte[] secretKey;

	public JwtTokenServiceImpl(@Value("${auth.jwt.secret}") String secret) {
		this.secretKey = validateSecret(secret);
	}

	@Override
	public TokenPrincipal parseAccessToken(String token) {
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
		if (tokenType != TokenType.ACCESS) {
			throw new InvalidTokenException("Token type is invalid");
		}

		Instant expiresAt = Instant.ofEpochSecond(readLongClaim(payload, "exp"));
		if (expiresAt.isBefore(Instant.now())) {
			throw new InvalidTokenException("Token is expired");
		}

		return new TokenPrincipal(
				UUID.fromString(readStringClaim(payload, "sub")),
				readStringClaim(payload, "username"),
				readStringClaim(payload, "role"),
				tokenType,
				Instant.ofEpochSecond(readLongClaim(payload, "iat")),
				expiresAt);
	}

	private Map<String, Object> decodePayload(String encodedPayload) {
		try {
			byte[] decodedPayload = BASE64_URL_DECODER.decode(encodedPayload);
			return objectMapper.readValue(decodedPayload, MAP_TYPE);
		} catch (Exception ex) {
			throw new InvalidTokenException("Token payload is invalid");
		}
	}

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

	private String readStringClaim(Map<String, Object> payload, String claimName) {
		Object value = payload.get(claimName);
		if (!(value instanceof String stringValue) || stringValue.isBlank()) {
			throw new InvalidTokenException("Token claim is invalid: " + claimName);
		}
		return stringValue;
	}

	private long readLongClaim(Map<String, Object> payload, String claimName) {
		Object value = payload.get(claimName);
		if (value instanceof Number numberValue) {
			return numberValue.longValue();
		}
		throw new InvalidTokenException("Token claim is invalid: " + claimName);
	}

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
}
