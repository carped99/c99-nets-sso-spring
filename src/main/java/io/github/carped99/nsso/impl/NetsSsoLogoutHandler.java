package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.common.constant.SSOConst;
import nets.sso.agent.web.v9.SSOAuthn;
import nets.sso.agent.web.v9.core.AuthnOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class NetsSsoLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request)
                .addSsoAgentType()
                .addHeader(SSOConst.OP, AuthnOperation.LOGOUT.getValue());

        // 1) SSO 인증 객체 초기화
        SSOAuthn authn = SSOAuthn.get(wrappedRequest, response);
        authn.authn();
    }
}
