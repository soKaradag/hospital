package com.hospital.hospital.audit.aspect;

import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.audit.model.AuditEvent;
import com.hospital.hospital.audit.model.AuditStatus;
import com.hospital.hospital.audit.service.AuditPublisher;
import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ForbiddenException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.common.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;

/*
- Bu aspect, @Audit ile işaretli iş akışlarını sarar ve başarı/başarısızlık sonucuna göre audit event üretir.
- Audit kodunu service metotlarının içine gömmemek için cross-cutting concern burada ayrıştırılır.
*/
@Aspect
@Component
public class AuditAspect {

	private final AuditPublisher auditPublisher;
	private final CurrentUserContext currentUserContext;

	public AuditAspect(AuditPublisher auditPublisher, CurrentUserContext currentUserContext) {
		this.auditPublisher = auditPublisher;
		this.currentUserContext = currentUserContext;
	}

	@Around("@annotation(com.hospital.hospital.audit.annotation.Audit)")
	public Object aroundAuditedMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		Audit audit = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Audit.class);
		try {
			Object result = joinPoint.proceed();
			publishEvent(audit, AuditStatus.SUCCESS, "Operation completed successfully", null);
			return result;
		} catch (Throwable throwable) {
			publishEvent(audit, resolveStatus(throwable), throwable.getMessage(), resolveErrorCode(throwable));
			throw throwable;
		}
	}

	// Exception tipine göre audit status belirlenir; böylece başarısızlık türleri daha sonra raporlanabilir.
	private AuditStatus resolveStatus(Throwable throwable) {
		if (throwable instanceof MethodArgumentNotValidException || throwable instanceof BindException) {
			return AuditStatus.VALIDATION_FAILURE;
		}
		if (throwable instanceof BusinessRuleViolationException
				|| throwable instanceof DuplicateResourceException
				|| throwable instanceof ResourceNotFoundException) {
			return AuditStatus.BUSINESS_FAILURE;
		}
		if (throwable instanceof UnauthorizedException) {
			return AuditStatus.UNAUTHORIZED_FAILURE;
		}
		if (throwable instanceof ForbiddenException) {
			return AuditStatus.FORBIDDEN_FAILURE;
		}
		return AuditStatus.SYSTEM_FAILURE;
	}

	// Hata kodlarını çözmek için kullanılır.
	private String resolveErrorCode(Throwable throwable) {
		if (throwable instanceof MethodArgumentNotValidException || throwable instanceof BindException) {
			return "VALIDATION_ERROR";
		}
		if (throwable instanceof BusinessRuleViolationException) {
			return "BUSINESS_RULE_VIOLATION";
		}
		if (throwable instanceof DuplicateResourceException) {
			return "DUPLICATE_RESOURCE";
		}
		if (throwable instanceof ResourceNotFoundException) {
			return "RESOURCE_NOT_FOUND";
		}
		if (throwable instanceof UnauthorizedException) {
			return "UNAUTHORIZED";
		}
		if (throwable instanceof ForbiddenException) {
			return "FORBIDDEN";
		}
		return "INTERNAL_SERVER_ERROR";
	}

	// Audit event'i oluşturur ve publisher'a iletir.
	private void publishEvent(Audit audit, AuditStatus status, String message, String errorCode) {
		TokenPrincipal principal = currentUserContext.isAuthenticated() ? currentUserContext.getPrincipal() : null;
		HttpServletRequest request = getCurrentRequest();
		auditPublisher.publish(new AuditEvent(
				audit.action(),
				audit.entity(),
				audit.description(),
				status,
				message,
				errorCode,
				principal != null ? principal.userId() : null,
				principal != null ? principal.role() : null,
				request != null ? request.getRequestURI() : null,
				request != null ? request.getMethod() : null,
				Instant.now()));
	}

	// Request context'ten HttpServletRequest'i alır.
	private HttpServletRequest getCurrentRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
			return servletRequestAttributes.getRequest();
		}
		return null;
	}
}
