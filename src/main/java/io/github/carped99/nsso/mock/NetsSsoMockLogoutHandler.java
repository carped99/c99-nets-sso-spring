package io.github.carped99.nsso.mock;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static io.github.carped99.nsso.mock.ConverterUtils.MOCK_COOKIE_NAME;

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

    private final CookieClearingLogoutHandler logoutHandler = new CookieClearingLogoutHandler(MOCK_COOKIE_NAME);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutHandler.logout(request, response, authentication);
    }
}
