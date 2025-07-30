package io.github.carped99.nsso;

import org.springframework.security.core.Authentication;

/**
 * NSSO 인증 서비스 인터페이스
 * 
 * <p>이 인터페이스는 NSSO 인증 토큰을 처리하여 실제 인증된 Authentication 객체를 생성하는 서비스를 정의합니다.
 * NSSO 에이전트와의 통신을 통해 사용자 인증을 수행합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>NSSO 인증 토큰 처리</li>
 *   <li>SSO 에이전트와의 통신</li>
 *   <li>인증된 사용자 정보 생성</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomNetsSsoAuthenticationService implements NetsSsoAuthenticationService {
 *     
 *     @Override
 *     public Authentication authenticate(NetsSsoAuthentication authentication) {
 *         // NSSO 인증 로직 구현
 *         return authenticatedAuthentication;
 *     }
 * }
 * }</pre>
 * 
 * @see NetsSsoAuthentication
 * @see Authentication
 * 
 * @author carped99
 * @since 0.0.1
 */
@FunctionalInterface
public interface NetsSsoAuthenticationService {
    /**
     * NSSO 인증 토큰을 처리하여 인증된 Authentication 객체를 반환합니다.
     * 
     * @param authentication 처리할 NSSO 인증 토큰
     * @return 인증된 Authentication 객체
     * @throws NetsSsoAuthenticationException 인증 실패 시
     */
    Authentication authenticate(NetsSsoAuthentication authentication);
}
