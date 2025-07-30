package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAuthenticationException;
import nets.sso.agent.web.v9.SSOStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

/**
 * NSSO 인증 예외 변환 유틸리티 클래스
 * 
 * <p>이 클래스는 NSSO 에이전트의 SSOStatus를 Spring Security의 적절한 AuthenticationException으로 변환하는 기능을 제공합니다.</p>
 * 
 * <p>지원하는 예외 변환:</p>
 * <ul>
 *   <li>11020003 - UsernameNotFoundException (사용자를 찾을 수 없음)</li>
 *   <li>11020004 - BadCredentialsException (잘못된 자격 증명)</li>
 *   <li>11020014 - DisabledException (비활성화된 계정)</li>
 *   <li>11020024 - DisabledException (비활성화된 계정)</li>
 *   <li>11020025 - CredentialsExpiredException (만료된 자격 증명)</li>
 *   <li>11050002 - LockedException (잠긴 계정)</li>
 *   <li>11070002 - CredentialsExpiredException (만료된 자격 증명)</li>
 *   <li>기타 - NetsSsoAuthenticationException (일반 NSSO 인증 예외)</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * SSOStatus status = authn.authnLoginStay();
 * if (status.getStatus() != AuthnStatus.SSO_SUCCESS) {
 *     throw ExceptionUtil.from(status);
 * }
 * }</pre>
 * 
 * @see NetsSsoAuthenticationException
 * @see SSOStatus
 * @see AuthenticationException
 * 
 * @author carped99
 * @since 0.0.1
 */
final class ExceptionUtil {
    /**
     * 유틸리티 클래스이므로 인스턴스화를 방지합니다.
     */
    private ExceptionUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * NSSO 상태를 Spring Security 인증 예외로 변환합니다.
     * 
     * <p>SSOStatus의 코드에 따라 적절한 AuthenticationException을 생성합니다.
     * 메시지가 없는 경우 기본 메시지를 생성합니다.</p>
     * 
     * @param status 변환할 NSSO 상태
     * @return 적절한 AuthenticationException 인스턴스
     */
    public static AuthenticationException from(SSOStatus status) {
        String message = status.getMessage();
        if (!StringUtils.hasLength(message)) {
            message = "SSO authentication failed with status: " + status.getCode();
        }

        if (status.getCode() == 11020003) {
            return new UsernameNotFoundException(message);
        }

        if (status.getCode() == 11020004) {
            return new BadCredentialsException(message);
        }

        if (status.getCode() == 11020014) {
            return new DisabledException(message);
        }

        if (status.getCode() == 11020024) {
            return new DisabledException(message);
        }

        if (status.getCode() == 11020025) {
            return new CredentialsExpiredException(message);
        }

        if (status.getCode() == 11050002) {
            return new LockedException(message);
        }

        if (status.getCode() == 11070002) {
            return new CredentialsExpiredException(message);
        }

        return new NetsSsoAuthenticationException(Integer.toString(status.getCode()), message);
    }
}
