package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAgentCheckService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.v9.SSOAuthn;

/**
 * NSSO 에이전트 체크 서비스의 기본 구현체
 * 
 * <p>이 클래스는 NSSO 에이전트의 상태 확인 요청을 처리하는 기본 구현체입니다.
 * NSSO 에이전트와의 통신을 통해 사용자 인증 상태를 확인하고 JSON 형태로 반환합니다.</p>
 * 
 * <p>처리 과정:</p>
 * <ol>
 *   <li>HTTP 요청을 NSSO 에이전트 타입으로 래핑</li>
 *   <li>SSO 인증 객체 초기화</li>
 *   <li>사용자 인증 상태 확인</li>
 *   <li>사용자 정보를 JSON 형태로 반환</li>
 * </ol>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentCheckService extends NetsSsoAgentCheckServiceImpl {
 *     // 커스텀 로직 추가 가능
 * }
 * }</pre>
 * 
 * @see NetsSsoAgentCheckService
 * @see SSOAuthn
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAgentCheckServiceImpl implements NetsSsoAgentCheckService {
    /**
     * 에이전트 체크 요청을 처리합니다.
     * 
     * <p>NSSO 에이전트를 통해 사용자의 인증 상태를 확인하고
     * 사용자 정보를 JSON 형태로 반환합니다.</p>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 사용자 정보 JSON 문자열
     */
    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();

        SSOAuthn authn = SSOAuthn.get(wrappedRequest, response);
        authn.authn();
        return authn.getUserJson();
    }
}
