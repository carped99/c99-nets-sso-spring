package io.github.carped99.nsso;

import nets.sso.agent.web.common.exception.SSOException;
import nets.sso.agent.web.common.exception.SSOExceptionCode;
import org.springframework.security.core.AuthenticationException;

/**
 * NSSO 인증 관련 예외 클래스
 *
 * <p>이 클래스는 NSSO 인증 과정에서 발생하는 예외를 나타냅니다.
 * Spring Security의 AuthenticationException을 확장하여 NSSO 특화된 오류 코드를 포함합니다.</p>
 *
 * <p>주요 특징:</p>
 * <ul>
 *   <li>Spring Security AuthenticationException 상속</li>
 *   <li>NSSO 오류 코드 포함</li>
 *   <li>다양한 생성자 제공</li>
 * </ul>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * try {
 *     // NSSO 인증 로직
 * } catch (NetsSsoAuthenticationException e) {
 *     String errorCode = e.getCode();
 *     String message = e.getMessage();
 *     // 오류 처리
 * }
 * }</pre>
 *
 * @author carped99
 * @see org.springframework.security.core.AuthenticationException
 * @see SSOException
 * @see SSOExceptionCode
 * @since 0.0.1
 */
public class NetsSsoAuthenticationException extends AuthenticationException {
    /**
     * NSSO 오류 코드
     */
    private final String code;

    /**
     * 기본 오류 코드("50000000")로 NetsSsoAuthenticationException을 생성합니다.
     *
     * @param msg 오류 메시지
     */
    public NetsSsoAuthenticationException(String msg) {
        super(msg);
        this.code = "50000000";
    }

    /**
     * SSOException과 메시지로 NetsSsoAuthenticationException을 생성합니다.
     *
     * @param exception 원본 SSOException
     * @param msg       오류 메시지
     */
    public NetsSsoAuthenticationException(SSOException exception, String msg) {
        super(msg, exception);
        this.code = Integer.toString(exception.getExceptionCode().getValue());
    }

    /**
     * SSOExceptionCode와 메시지로 NetsSsoAuthenticationException을 생성합니다.
     *
     * @param code SSOExceptionCode
     * @param msg  오류 메시지
     */
    public NetsSsoAuthenticationException(SSOExceptionCode code, String msg) {
        super(msg);
        this.code = Integer.toString(code.getValue());
    }

    /**
     * 사용자 정의 오류 코드와 메시지로 NetsSsoAuthenticationException을 생성합니다.
     *
     * @param code 오류 코드
     * @param msg  오류 메시지
     */
    public NetsSsoAuthenticationException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * 사용자 정의 오류 코드, 메시지, 원인으로 NetsSsoAuthenticationException을 생성합니다.
     *
     * @param code  오류 코드
     * @param msg   오류 메시지
     * @param cause 원인 예외
     */
    public NetsSsoAuthenticationException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    /**
     * NSSO 오류 코드를 반환합니다.
     *
     * @return 오류 코드
     */
    public String getCode() {
        return code;
    }
}
