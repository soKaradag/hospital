package com.hospital.hospital.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
- Bu annotation, endpoint seviyesinde gerekli permission kodlarını belirtir.
- Roller veri kaydı olarak kalır; erişim kararı permission kodları üzerinden verilir.
*/
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

	String[] value();
}
