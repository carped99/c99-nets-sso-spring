package io.github.carped99.nsso.configure;

import io.github.carped99.nsso.NetsSsoAgentFilter;
import io.github.carped99.nsso.NetsSsoAuthenticationService;
import io.github.carped99.nsso.NetsSsoTokenFilter;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import static io.github.carped99.nsso.NetsSsoUtils.normalizePath;
import static io.github.carped99.nsso.configure.NetsSsoConfigurerUtils.getBean;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * NSSO 에이전트 필터 설정 클래스
 *
 * <p>이 클래스는 NSSO 에이전트 필터를 Spring Security 설정에 추가하는 컨피규러입니다.
 * 다양한 에이전트 서비스들(체크, 설정, 중복 로그인, 2FA, 키)을 설정하고
 * 해당하는 URL 패턴과 매핑합니다.</p>
 *
 * <p>설정되는 서비스들:</p>
 * <ul>
 *   <li>체크 서비스 - /check</li>
 *   <li>설정 서비스 - /config</li>
 *   <li>중복 로그인 서비스 - /duplication</li>
 *   <li>2FA 서비스 - /tfa</li>
 *   <li>키 서비스 - /key</li>
 * </ul>
 *
 * <p>모든 서비스는 POST 요청으로 처리되며, CSRF 필터 이후에 추가됩니다.</p>
 *
 * @author carped99
 * @see NetsSsoAgentFilter
 * @see SecurityConfigurerAdapter
 * @since 0.0.1
 */
public class NetsSsoTokenFilterConfigurer<B extends HttpSecurityBuilder<B>> extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {
    private String prefixPath;
    private RequestMatcher requestMatcher;
    @Nullable
    private AuthenticationSuccessHandler successHandler;
    @Nullable
    private AuthenticationFailureHandler failureHandler;

    /**
     * NSSO 에이전트 필터를 Spring Security 설정에 추가합니다.
     *
     * <p>설정 과정:</p>
     * <ol>
     *   <li>NetsSsoAgentFilter 인스턴스 생성</li>
     *   <li>각 서비스별 RequestMatcher 생성 (POST 요청)</li>
     *   <li>필터에 각 서비스 설정</li>
     *   <li>전체 RequestMatcher 생성</li>
     *   <li>CSRF 필터 이후에 필터 추가</li>
     * </ol>
     *
     * @param builder HttpSecurity 빌더
     * @throws Exception 설정 중 오류 발생 시
     */
    @Override
    public void configure(B builder) throws Exception {
        NetsSsoAuthenticationService authenticationService = getBean(builder, NetsSsoAuthenticationService.class);
        Assert.state(authenticationService != null, "NetsSsoAuthenticationService required");

        UserDetailsService userDetailsService = getBean(builder, UserDetailsService.class);
        Assert.state(userDetailsService != null, "UserDetailsService required");

        this.requestMatcher = antMatcher(normalizePath(this.prefixPath, "/token"));
        var filter = new NetsSsoTokenFilter(this.requestMatcher, authenticationService, userDetailsService);

        Assert.state(successHandler != null, "successHandler must not be null");
        Assert.state(failureHandler != null, "failureHandler must not be null");
        filter.setSuccessHandler(this.successHandler);
        filter.setFailureHandler(this.failureHandler);

        builder.addFilterAfter(postProcess(filter), AuthenticationFilter.class);
    }

    /**
     * 토큰 인증 성공 핸들러를 설정한다.
     *
     * @param successHandler 인증 성공 핸들러
     * @return 현재 컨피규러 인스턴스
     */
    public NetsSsoTokenFilterConfigurer<B> successHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler must not be null");
        this.successHandler = successHandler;
        return this;
    }

    /**
     * 토큰 인증 실패 핸들러를 설정한다.
     *
     * @param failureHandler 인증 실패 핸들러
     * @return 현재 컨피규러 인스턴스
     */
    public NetsSsoTokenFilterConfigurer<B> failureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler must not be null");
        this.failureHandler = failureHandler;
        return this;
    }

    /**
     * 설정된 RequestMatcher를 반환합니다.
     *
     * @return 모든 에이전트 서비스 요청을 매칭하는 RequestMatcher
     */
    RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    /**
     * URL 접두사를 설정합니다.
     *
     * @param prefixPath URL 접두사
     */
    void setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    @Nullable
    AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    @Nullable
    AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }
}
