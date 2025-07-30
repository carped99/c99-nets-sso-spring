package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAgentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.common.exception.SSOException;
import nets.sso.agent.web.v9.SSOAuthn;
import nets.sso.agent.web.v9.SSOMfa;
import nets.sso.agent.web.v9.SSOStatus;

public class NetsSsoAgentServiceImpl implements NetsSsoAgentService {

    @Override
    public String check(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();
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
