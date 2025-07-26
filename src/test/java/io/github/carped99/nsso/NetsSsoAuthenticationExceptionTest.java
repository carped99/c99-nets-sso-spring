package io.github.carped99.nsso;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NetsSsoAuthenticationException 클래스의 단위 테스트
 * 
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoAuthenticationExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateExceptionWithDefaultCode() {
        // given
        String message = "인증 실패";

        // when
        NetsSsoAuthenticationException exception = new NetsSsoAuthenticationException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo("50000000");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructor_WithCustomCodeAndMessage_ShouldCreateExceptionWithCustomCode() {
        // given
        String code = "CUSTOM_ERROR_001";
        String message = "사용자 정의 오류";

        // when
        NetsSsoAuthenticationException exception = new NetsSsoAuthenticationException(code, message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructor_WithCustomCodeMessageAndCause_ShouldCreateExceptionWithAllFields() {
        // given
        String code = "CUSTOM_ERROR_002";
        String message = "사용자 정의 오류 with cause";
        RuntimeException cause = new RuntimeException("원인 예외");

        // when
        NetsSsoAuthenticationException exception = new NetsSsoAuthenticationException(code, message, cause);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void getCode_ShouldReturnCorrectCode() {
        // given
        String expectedCode = "TEST_CODE_123";
        NetsSsoAuthenticationException exception = new NetsSsoAuthenticationException(expectedCode, "테스트 메시지");

        // when
        String actualCode = exception.getCode();

        // then
        assertThat(actualCode).isEqualTo(expectedCode);
    }

    @Test
    void exception_ShouldBeInstanceOfAuthenticationException() {
        // given
        NetsSsoAuthenticationException exception = new NetsSsoAuthenticationException("테스트");

        // when & then
        assertThat(exception).isInstanceOf(org.springframework.security.core.AuthenticationException.class);
    }
} 