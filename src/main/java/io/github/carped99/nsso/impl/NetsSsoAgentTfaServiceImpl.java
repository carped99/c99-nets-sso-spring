package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAgentTfaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.v9.SSOAuthn;
import nets.sso.agent.web.v9.SSOMfa;
import nets.sso.agent.web.v9.SSOStatus;

/**
 * NSSO 에이전트 2FA 서비스의 기본 구현체
 * 
 * <p>이 클래스는 NSSO 에이전트의 2단계 인증 요청을 처리하는 기본 구현체입니다.
 * NSSO 에이전트와의 통신을 통해 2FA 정보를 조회하고 JSON 형태로 반환합니다.</p>
 * 
 * <p>처리 과정:</p>
 * <ol>
 *   <li>HTTP 요청을 NSSO 에이전트 타입으로 래핑</li>
 *   <li>SSO 인증 객체 초기화</li>
 *   <li>2FA 정보 조회</li>
 *   <li>2FA 정보 또는 오류 정보를 JSON 형태로 반환</li>
 * </ol>
 * 
 * <p>응답 형식:</p>
 * <ul>
 *   <li>성공 시: 2FA ID, 디바이스, 메서드, 타임아웃 등의 정보 포함</li>
 *   <li>실패 시: 오류 코드와 메시지 포함</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentTfaService extends NetsSsoAgentTfaServiceImpl {
 *     // 커스텀 로직 추가 가능
 * }
 * }</pre>
 * 
 * @see NetsSsoAgentTfaService
 * @see SSOAuthn
 * @see SSOMfa
 * @see SSOStatus
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAgentTfaServiceImpl implements NetsSsoAgentTfaService {
    /**
     * 2FA 요청을 처리합니다.
     * 
     * <p>NSSO 에이전트를 통해 2FA 정보를 조회하고 JSON 형태로 반환합니다.
     * 2FA 정보가 있으면 상세 정보를, 없으면 오류 정보를 반환합니다.</p>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 2FA 정보 또는 오류 정보 JSON 문자열
     */
    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
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
