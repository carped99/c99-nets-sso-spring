package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * NSSO 리프레시 토큰 처리 필터
 * 
 * <p>이 필터는 NSSO 리프레시 토큰 요청을 처리하는 Spring Security 필터입니다.
 * 만료된 액세스 토큰을 갱신하기 위한 리프레시 토큰을 처리합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>리프레시 토큰 요청 처리</li>
 *   <li>NSSO 인증 토큰 생성</li>
 *   <li>Spring Security 인증 매니저를 통한 인증 처리</li>
 * </ul>
 * 
 * <p>기본 URL: /nsso/refresh_token</p>
 * 
 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 * @see NetsSsoAuthentication
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoRefreshTokenFilter extends AbstractAuthenticationProcessingFilter {
    /**
     * 기본 URL("/nsso/refresh_token")로 NetsSsoRefreshTokenFilter를 생성합니다.
     */
    public NetsSsoRefreshTokenFilter() {
        super("/nsso/refresh_token");
    }

    /**
     * 지정된 RequestMatcher로 NetsSsoRefreshTokenFilter를 생성합니다.
     * 
     * @param requestMatcher 요청을 매칭하는 RequestMatcher
     */
    public NetsSsoRefreshTokenFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    /**
     * NSSO 리프레시 토큰 인증을 시도합니다.
     * 
     * <p>NSSO 인증 토큰을 생성하고 Spring Security 인증 매니저를 통해 인증을 수행합니다.</p>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 실패 시
     */
    @Nullable
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        var authentication = NetsSsoAuthentication.unauthenticated(request, response);
        return this.getAuthenticationManager().authenticate(authentication);
    }
}
