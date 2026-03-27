package com.hospital.hospital.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hospital.hospital.auth.model.Role;

/*
- Bu annotation, bir endpoint veya sınıf için hangi rollerin erişebileceğini belirtir.
- Spring Security kullanılmadığı için rol kuralı burada açıkça işaretlenir.
- Method seviyesindeki kullanım, class seviyesindeki kuralın üstüne yazılabilir.
*/
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

	Role[] value();
}
