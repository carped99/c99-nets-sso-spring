package io.github.carped99.nsso.mock;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * NSSO Mock 로그아웃 핸들러
 *
 * <p>이 클래스는 NSSO Mock 환경에서 로그아웃 처리를 담당합니다.
 * NSSO 인증 세션을 종료하고 관련 쿠키를 제거합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>NSSO 인증 세션 종료</li>
 *   <li>관련 쿠키 제거</li>
 * </ul>
 *
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoMockLogoutHandler implements LogoutHandler {
    protected final Log log = LogFactory.getLog(getClass());
    private final CookieClearingLogoutHandler logoutHandler = new CookieClearingLogoutHandler("nsso-mock-auth");

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("Clear cookies: nsso-mock-auth");
        logoutHandler.logout(request, response, authentication);
    }
}
