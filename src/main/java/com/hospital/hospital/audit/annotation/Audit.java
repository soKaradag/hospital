package com.hospital.hospital.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
- Bu annotation, audit kaydı tutulması gereken iş akışlarını işaretlemek için kullanılır.
- Method seviyesinde kullanılarak hangi işlemin hangi action ve entity adıyla loglanacağı açıkça belirtilir.
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

	String action();

	String entity();

	String description() default "";
}
