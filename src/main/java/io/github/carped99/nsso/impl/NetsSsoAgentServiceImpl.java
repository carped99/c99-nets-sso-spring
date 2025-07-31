package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAgentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.common.constant.SSOConst;
import nets.sso.agent.web.common.exception.SSOException;
import nets.sso.agent.web.v9.SSOAuthn;
import nets.sso.agent.web.v9.SSOMfa;
import nets.sso.agent.web.v9.SSOStatus;
import nets.sso.agent.web.v9.core.AuthnOperation;

/**
 * NSSO 에이전트 서비스의 기본 구현체
 *
 * <p>이 클래스는 NSSO 에이전트와의 통신을 통해 다양한 요청을 처리합니다.</p>
 *
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAgentServiceImpl implements NetsSsoAgentService {

    @Override
    public String check(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request)
                .addSsoAgentType()
                .addHeader(SSOConst.OP, AuthnOperation.AUTHN.getValue());

        SSOAuthn authn = SSOAuthn.get(wrappedRequest, response);
        authn.authn();
        return authn.getUserJson();
    }

    @Override
    public String config(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();
        return SSOAuthn.get(wrappedRequest, response).getConfJson();
    }

    @Override
    public String duplicate(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();

        try {
            return SSOAuthn.get(wrappedRequest, response).getDup().toJson();
        } catch (SSOException e) {
            return e.toJson();
        }
    }

    @Override
    public String key(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();

        try {
            return SSOAuthn.get(wrappedRequest, response).getPublicKey();
        } catch (SSOException e) {
            return e.toJson();
        }
    }

    @Override
    public String tfa(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();

        SSOAuthn authn = SSOAuthn.get(wrappedRequest, response);
        SSOMfa mfa = authn.getMfa();

        StringBuilder sb = new StringBuilder();
        if (mfa != null) {
            sb.append("{")
                    .append("\"result\": true,")
                    .append("\"errorCode\": 0,")
                    .append("\"tfaID\": \"").append(mfa.getMfaID()).append("\",")
                    .append("\"targetYN\": true,")
                    .append("\"device\": \"").append(mfa.getMfaDevice()).append("\",")
                    .append("\"code\": \"\",")
                    .append("\"method\": \"").append(mfa.getMfaMethod()).append("\",")
                    .append("\"timeoutMinutes\": ").append(mfa.getMfaTimeoutMin())
                    .append("}");
        } else {
            SSOStatus status = authn.getLastStatus();
            sb.append("{")
                    .append("\"result\": false,")
                    .append("\"errorCode\": ").append(status.getCode()).append(",")
                    .append("\"errorMessage\": \"").append(status.getMessage()).append("\",")
                    .append("}");
        }
        return sb.toString();
    }
}
