package io.github.carped99.nsso.mock;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * NSSO Mock 인증 성공 핸들러
 *
 * <p>이 클래스는 NSSO Mock 인증 성공 시 사용자 이름을 쿠키에 저장하고,
 * 지정된 핸들러를 호출하여 추가 처리를 수행합니다.</p>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Bean
 * public AuthenticationSuccessHandler authenticationSuccessHandler() {
 *     return new NetsSsoMockAuthenticationSuccessHandler(new DefaultAuthenticationSuccessHandler());
 * }
 * }</pre>
 *
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoMockAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final AuthenticationFailureHandler handler;

    /**
     * 생성자
     *
     * @param handler 인증 실패 핸들러
     */
    public NetsSsoMockAuthenticationFailureHandler(AuthenticationFailureHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

    }
}
