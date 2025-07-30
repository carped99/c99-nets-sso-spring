package io.github.carped99.nsso.mock;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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
public class NetsSsoMockAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    protected final Log log = LogFactory.getLog(getClass());
    private final AuthenticationSuccessHandler handler;

    public NetsSsoMockAuthenticationSuccessHandler(AuthenticationSuccessHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String value = ConverterUtils.encodeUsername(authentication.getName());

        log.info("Set-Cookie: nsso-mock-auth=" + value);

        ResponseCookie cookie = ResponseCookie.from("nsso-mock-auth", value)
                .path("/")
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        this.handler.onAuthenticationSuccess(request, response, authentication);
    }
}
