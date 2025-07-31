package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * NSSO 인증에 사용되는 Spring Security 인증 토큰
 *
 * <p>이 클래스는 NSSO 에이전트와 Spring Security 간의 인증 정보를 전달하는 토큰입니다.
 * 인증되지 않은 상태와 인증된 상태를 모두 지원하며, HTTP 요청/응답 객체를 포함할 수 있습니다.</p>
 *
 * <p>주요 특징:</p>
 * <ul>
 *   <li>Spring Security의 AbstractAuthenticationToken을 상속</li>
 *   <li>인증 전후 상태를 모두 지원</li>
 *   <li>HTTP 요청/응답 객체 보관</li>
 *   <li>불변 객체로 설계</li>
 * </ul>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * // 인증되지 않은 토큰 생성
 * NetsSsoAuthentication unauthenticated =
 *     NetsSsoAuthentication.unauthenticated(request, response);
 *
 * // 인증된 토큰 생성
 * NetsSsoAuthentication authenticated =
 *     NetsSsoAuthentication.authenticated(principal, authorities);
 * }</pre>
 *
 * @author carped99
 * @since 0.0.1
 */
public final class NetsSsoAuthentication extends AbstractAuthenticationToken {
    /**
     * 인증 주체 (사용자 정보)
     */
    private final Object principal;
    /**
     * 인증 자격 증명
     */
    private final Object credentials;
    /**
     * HTTP 요청 객체 (인증되지 않은 토큰에서만 사용)
     */
    private final HttpServletRequest request;
    /**
     * HTTP 응답 객체 (인증되지 않은 토큰에서만 사용)
     */
    private final HttpServletResponse response;

    /**
     * 인증된 사용자 정보로 NetsSsoAuthentication 토큰을 생성합니다.
     *
     * @param principal   사용자 주체 정보 (보통 사용자 ID 또는 사용자 객체)
     * @param authorities 사용자의 권한 목록
     * @return 인증된 상태의 NetsSsoAuthentication 토큰
     * @throws IllegalArgumentException principal 또는 authorities가 null인 경우
     */
    public static NetsSsoAuthentication authenticated(Object principal, @Nullable Collection<? extends GrantedAuthority> authorities) {
        return new NetsSsoAuthentication(principal, authorities);
    }

    /**
     * 인증되지 않은 상태의 NetsSsoAuthentication 토큰을 생성합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증되지 않은 상태의 NetsSsoAuthentication 토큰
     * @throws IllegalArgumentException request 또는 response가 null인 경우
     */
    public static NetsSsoAuthentication unauthenticated(HttpServletRequest request, HttpServletResponse response) {
        return new NetsSsoAuthentication(request, response);
    }

    private NetsSsoAuthentication(HttpServletRequest request, HttpServletResponse response) {
        super(null);
        Assert.notNull(request, "request must not be null");
        Assert.notNull(response, "response must not be null");

        this.principal = null;
        this.credentials = null;
        this.request = request;
        this.response = response;
    }

    private NetsSsoAuthentication(Object principal, @Nullable Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.notNull(principal, "principal must not be null");
        this.principal = principal;
        this.credentials = null;
        this.request = null;
        this.response = null;
        setAuthenticated(true);
    }

    /**
     * 인증 주체를 반환합니다.
     *
     * @return 인증 주체 객체 (사용자 ID 또는 사용자 객체)
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }

    /**
     * 인증 자격 증명을 반환합니다.
     *
     * @return 인증 자격 증명 (NSSO에서는 일반적으로 null)
     */
    @Override
    public Object getCredentials() {
        return credentials;
    }

    /**
     * HTTP 요청 객체를 반환합니다.
     *
     * @return HTTP 요청 객체 (인증되지 않은 토큰에서만 사용 가능)
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * HTTP 응답 객체를 반환합니다.
     *
     * @return HTTP 응답 객체 (인증되지 않은 토큰에서만 사용 가능)
     */
    public HttpServletResponse getResponse() {
        return response;
    }
}
