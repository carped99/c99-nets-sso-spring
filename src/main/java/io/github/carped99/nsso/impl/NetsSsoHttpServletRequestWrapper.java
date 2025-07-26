package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

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
 * @see jakarta.servlet.http.HttpServletRequestWrapper
 * 
 * @author tykim
 * @since 0.0.0
 */
final class NetsSsoHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>();

    /**
     * 주어진 HTTP 요청으로 래퍼를 생성합니다.
     * 
     * @param request 래핑할 HTTP 요청
     */
    public NetsSsoHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * SSO 에이전트 타입 헤더를 추가합니다.
     * 
     * <p>"SSOAgent-Type" 헤더가 없는 경우 "SPA" 값으로 설정합니다.</p>
     * 
     * @return 현재 래퍼 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoHttpServletRequestWrapper addSsoAgentType() {
        if (!StringUtils.hasLength(getHeader("SSOAgent-Type"))) {
            addHeader("SSOAgent-Type", "SPA");
        }
        return this;
    }

    /**
     * 커스텀 헤더를 추가합니다.
     * 
     * @param name 헤더 이름
     * @param value 헤더 값
     */
    public void addHeader(String name, String value) {
        this.headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    /**
     * 지정된 이름의 헤더 값을 반환합니다.
     * 
     * <p>커스텀 헤더가 있으면 해당 값을 반환하고, 없으면 원본 요청의 헤더를 반환합니다.</p>
     * 
     * @param name 헤더 이름
     * @return 헤더 값 (없으면 null)
     */
    @Override
    public String getHeader(String name) {
        var values = this.headers.get(name);
        if (CollectionUtils.isEmpty(values)) {
            return super.getHeader(name);
        }
        return values.get(0);
    }

    /**
     * 지정된 이름의 모든 헤더 값을 반환합니다.
     * 
     * <p>커스텀 헤더가 있으면 해당 값들을 반환하고, 없으면 원본 요청의 헤더를 반환합니다.</p>
     * 
     * @param name 헤더 이름
     * @return 헤더 값들의 열거형
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        var values = this.headers.get(name);
        if (CollectionUtils.isEmpty(values)) {
            return Objects.requireNonNullElseGet(super.getHeaders(name), Collections::emptyEnumeration);
        }
        return Collections.enumeration(values);
    }

    /**
     * 모든 헤더 이름을 반환합니다.
     * 
     * <p>커스텀 헤더와 원본 요청의 헤더를 모두 포함합니다.</p>
     * 
     * @return 모든 헤더 이름의 열거형
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        HashSet<String> names = new HashSet<>(this.headers.keySet());
        Enumeration<String> headerNames = super.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                names.add(headerNames.nextElement());
            }
        }
        return Collections.enumeration(names);
    }
}
