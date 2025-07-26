package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAuthenticationException;
import nets.sso.agent.web.v9.SSOStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * ExceptionUtil 클래스의 단위 테스트
 *
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class ExceptionUtilTest {

    @Mock
    private SSOStatus ssoStatus;

    @Test
    void from_WithGeneralErrorCode_ShouldReturnNetsSsoAuthenticationException() {
        // given
        when(ssoStatus.getCode()).thenReturn(99999999);
        when(ssoStatus.getMessage()).thenReturn("일반 오류");

        // when
        AuthenticationException exception = ExceptionUtil.from(ssoStatus);

        // then
        assertThat(exception).isInstanceOf(NetsSsoAuthenticationException.class);
        NetsSsoAuthenticationException nssoException = (NetsSsoAuthenticationException) exception;
        assertThat(nssoException.getCode()).isEqualTo("99999999");
        assertThat(nssoException.getMessage()).isEqualTo("일반 오류");
    }

    @Test
    void from_WithEmptyMessage_ShouldUseDefaultMessage() {
        // given
        when(ssoStatus.getCode()).thenReturn(99999999);
        when(ssoStatus.getMessage()).thenReturn("");

        // when
        AuthenticationException exception = ExceptionUtil.from(ssoStatus);

        // then
        assertThat(exception).isInstanceOf(NetsSsoAuthenticationException.class);
        NetsSsoAuthenticationException nssoException = (NetsSsoAuthenticationException) exception;
        assertThat(nssoException.getCode()).isEqualTo("99999999");
        assertThat(nssoException.getMessage()).contains("SSO authentication failed with status: 99999999");
    }

    @Test
    void from_WithNullMessage_ShouldUseDefaultMessage() {
        // given
        when(ssoStatus.getCode()).thenReturn(99999999);
        when(ssoStatus.getMessage()).thenReturn(null);

        // when
        AuthenticationException exception = ExceptionUtil.from(ssoStatus);

        // then
        assertThat(exception).isInstanceOf(NetsSsoAuthenticationException.class);
        NetsSsoAuthenticationException nssoException = (NetsSsoAuthenticationException) exception;
        assertThat(nssoException.getCode()).isEqualTo("99999999");
        assertThat(nssoException.getMessage()).contains("SSO authentication failed with status: 99999999");
    }

    @Test
    void exception_ShouldBeInstanceOfAuthenticationException() {
        // given
        when(ssoStatus.getCode()).thenReturn(99999999);
        when(ssoStatus.getMessage()).thenReturn("테스트 오류");

        // when
        AuthenticationException exception = ExceptionUtil.from(ssoStatus);

        // then
        assertThat(exception).isInstanceOf(AuthenticationException.class);
    }
} 