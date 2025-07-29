package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;

/**
 * NSSO 에이전트 요청을 위한 HTTP 요청 래퍼
 * 
 * <p>이 클래스는 NSSO 에이전트와의 통신을 위해 HTTP 요청을 래핑하고
 * 필요한 헤더를 추가하는 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>HTTP 요청 래핑</li>
 *   <li>SSO 에이전트 타입 헤더 추가</li>
 *   <li>커스텀 헤더 관리</li>
 *   <li>대소문자 구분 없는 헤더 처리</li>
 * </ul>
 * 
 * <p>기본적으로 "SSOAgent-Type" 헤더를 "SPA" 값으로 설정합니다.</p>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * HttpServletRequest request = ...;
 * var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request)
 *     .addSsoAgentType();
 * wrappedRequest.addHeader("Custom-Header", "value");
 * }</pre>
 * 
 * @see HttpServletRequestWrapper
 * 
 * @author tykim
 * @since 0.0.0
 */
final class NetsSsoHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private final Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>();

    /**
     * 주어진 HTTP 응답으로 래퍼를 생성합니다.
     *
     * @param response 래핑할 HTTP 응답
     */
    public NetsSsoHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}
