package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAgentConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.v9.SSOAuthn;

/**
 * NSSO 에이전트 설정 서비스의 기본 구현체
 * 
 * <p>이 클래스는 NSSO 에이전트의 설정 관리 요청을 처리하는 기본 구현체입니다.
 * NSSO 에이전트와의 통신을 통해 설정 정보를 조회하고 JSON 형태로 반환합니다.</p>
 * 
 * <p>처리 과정:</p>
 * <ol>
 *   <li>HTTP 요청을 NSSO 에이전트 타입으로 래핑</li>
 *   <li>SSO 인증 객체 초기화</li>
 *   <li>에이전트 설정 정보 조회</li>
 *   <li>설정 정보를 JSON 형태로 반환</li>
 * </ol>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentConfigService extends NetsSsoAgentConfigServiceImpl {
 *     // 커스텀 로직 추가 가능
 * }
 * }</pre>
 * 
 * @see NetsSsoAgentConfigService
 * @see SSOAuthn
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAgentConfigServiceImpl implements NetsSsoAgentConfigService {
    /**
     * 에이전트 설정 요청을 처리합니다.
     * 
     * <p>NSSO 에이전트를 통해 설정 정보를 조회하고
     * JSON 형태로 반환합니다.</p>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 설정 정보 JSON 문자열
     */
    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request).addSsoAgentType();

        return SSOAuthn.get(wrappedRequest, response).getConfJson();
    }
}
