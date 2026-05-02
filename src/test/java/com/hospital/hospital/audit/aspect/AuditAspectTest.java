package com.hospital.hospital.audit.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.audit.model.AuditEvent;
import com.hospital.hospital.audit.model.AuditStatus;
import com.hospital.hospital.audit.service.AuditPublisher;
import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.auth.token.TokenType;
import com.hospital.hospital.common.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

	@Mock
	private AuditPublisher auditPublisher;

	@Mock
	private CurrentUserContext currentUserContext;

	@Mock
	private ProceedingJoinPoint joinPoint;

	@Mock
	private MethodSignature methodSignature;

	@InjectMocks
	private AuditAspect auditAspect;

	@Test
	void aroundAuditedMethodShouldPublishSuccessEvent() throws Throwable {
		Method method = AuditTarget.class.getDeclaredMethod("auditedSuccess");
		TokenPrincipal principal = new TokenPrincipal(
				UUID.randomUUID(), "doctor1", Role.DOCTOR, TokenType.ACCESS, java.time.Instant.now(), java.time.Instant.now().plusSeconds(60));

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(joinPoint.proceed()).thenReturn("ok");
		when(currentUserContext.isAuthenticated()).thenReturn(true);
		when(currentUserContext.getPrincipal()).thenReturn(principal);

		Object result = auditAspect.aroundAuditedMethod(joinPoint);

		assertEquals("ok", result);
		ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
		verify(auditPublisher).publish(eventCaptor.capture());
		assertEquals(AuditStatus.SUCCESS, eventCaptor.getValue().status());
		assertEquals("TEST_SUCCESS", eventCaptor.getValue().action());
		assertEquals(principal.userId(), eventCaptor.getValue().actorUserId());
	}

	@Test
	void aroundAuditedMethodShouldPublishUnauthorizedFailureEvent() throws Throwable {
		Method method = AuditTarget.class.getDeclaredMethod("auditedFailure");

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(joinPoint.proceed()).thenThrow(new UnauthorizedException("Unauthorized"));
		when(currentUserContext.isAuthenticated()).thenReturn(false);

		assertThrows(UnauthorizedException.class, () -> auditAspect.aroundAuditedMethod(joinPoint));

		ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
		verify(auditPublisher).publish(eventCaptor.capture());
		assertEquals(AuditStatus.UNAUTHORIZED_FAILURE, eventCaptor.getValue().status());
		assertEquals("UNAUTHORIZED", eventCaptor.getValue().errorCode());
	}

	static class AuditTarget {
		@Audit(action = "TEST_SUCCESS", entity = "TEST")
		void auditedSuccess() {
		}

		@Audit(action = "TEST_FAILURE", entity = "TEST")
		void auditedFailure() {
		}
	}
}
