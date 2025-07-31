package io.github.carped99.nsso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
 * @author carped99
 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 * @see NetsSsoAuthentication
 * @since 0.0.1
 */
public class NetsSsoTokenFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher;
    private final NetsSsoAuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    /**
     * 지정된 RequestMatcher로 NetsSsoRefreshTokenFilter를 생성합니다.
     *
     * @param requestMatcher 요청을 매칭하는 RequestMatcher
     */
    public NetsSsoTokenFilter(RequestMatcher requestMatcher, NetsSsoAuthenticationService authenticationService, UserDetailsService userDetailsService) {
        Assert.notNull(requestMatcher, "requestMatcher may not be null");
        Assert.notNull(authenticationService, "authenticationService may not be null");
        this.requestMatcher = requestMatcher;
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var authenticated = authenticationService.authenticate(request, response);
            var userDetails = userDetailsService.loadUserByUsername(authenticated.getName());

            var result = NetsSsoAuthentication.authenticated(userDetails, userDetails.getAuthorities());
            result.setDetails(authenticated.getDetails());

            successHandler.onAuthenticationSuccess(request, response, result);
        } catch (AuthenticationException e) {
            failureHandler.onAuthenticationFailure(request, response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !requestMatcher.matches(request);
    }

    /**
     * 토큰 인증 성공 핸들러를 설정한다.
     *
     * @param successHandler 인증 성공 핸들러
     */
    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler must not be null");
        this.successHandler = successHandler;
    }

    /**
     * 토큰 인증 실패 핸들러를 설정한다.
     *
     * @param failureHandler 인증 실패 핸들러
     */
    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler must not be null");
        this.failureHandler = failureHandler;
    }
}
