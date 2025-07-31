package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.common.constant.SSOConst;
import nets.sso.agent.web.v9.SSOAuthn;
import nets.sso.agent.web.v9.SSOUrl;
import nets.sso.agent.web.v9.core.AuthnOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * NSSO 로그아웃 핸들러
 *
 * <p>이 클래스는 NSSO 에이전트와의 로그아웃 처리를 담당합니다.
 * SSO 인증 객체를 초기화하고 로그아웃 작업을 수행합니다.</p>
 *
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var wrappedRequest = wrappedRequest(request, response);

        // BODY를 무시하고, 쿠키 제거하기 위해
        var wrappedResponse = new NetsSsoNoBodyHttpServletResponseWrapper(response);

        // 1) SSO 인증 객체 초기화
        SSOAuthn authn = SSOAuthn.get(wrappedRequest, wrappedResponse);
        authn.authn();
    }

    private HttpServletRequest wrappedRequest(HttpServletRequest request, HttpServletResponse response) {
        SSOAuthn authn = new SSOAuthn(request, response);
        String appCode = authn.getAppCode();
        SSOUrl ssoUrl = authn.getUrl();
        String appUrl = ssoUrl.getAppUrl();


        NetsSsoHttpServletRequestWrapper wrapper = new NetsSsoHttpServletRequestWrapper(request)
                .addSsoAgentType();

        if (wrapper.getParameter(SSOConst.OP) == null) {
            wrapper.addParameter(SSOConst.OP, AuthnOperation.LOGOUT.getValue());
        }

        if (wrapper.getParameter(SSOConst.SITE_ID) == null) {
            wrapper.addParameter(SSOConst.SITE_ID, appCode);
        }

        if (wrapper.getParameter(SSOConst.RETURN_URL) == null) {
            wrapper.addParameter(SSOConst.RETURN_URL, appUrl);
        }

        return wrapper;
    }
}
