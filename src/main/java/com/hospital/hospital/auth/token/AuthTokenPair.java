package com.hospital.hospital.auth.token;

/*
 * Access token ve refresh token çiftini taşır.
 * Login veya token yenileme sonrasında istemciye iki tokenın birlikte dönülmesi için kullanılır.
 * Bu projede refresh token response body içinde döndürülür.
 * Gerçek üretim ortamlarında, özellikle web uygulamalarında, refresh token'ın
 * httpOnly cookie ile taşınması daha güvenli bir tercih olabilir.
 */
public record AuthTokenPair(
		String accessToken,
		String refreshToken) {
}
