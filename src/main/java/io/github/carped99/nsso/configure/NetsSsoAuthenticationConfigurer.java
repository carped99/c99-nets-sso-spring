package io.github.carped99.nsso.configure;

import io.github.carped99.nsso.NetsSsoAuthenticationFilter;
import io.github.carped99.nsso.NetsSsoLogoutFilter;
import io.github.carped99.nsso.NetsSsoRefreshTokenFilter;
import io.github.carped99.nsso.impl.NetsSsoLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.carped99.nsso.configure.NetsSsoConfigurerUtils.normalizePath;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * NSSO 인증 설정의 메인 컨피규러
 * 
 * <p>이 클래스는 NSSO와 Spring Security의 통합을 위한 메인 설정 클래스입니다.
 * 액세스 토큰 필터, 리프레시 토큰 필터, 에이전트 필터 등을 설정하고
 * 전체적인 NSSO 인증 플로우를 구성합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>액세스 토큰 필터 설정</li>
 *   <li>리프레시 토큰 필터 설정</li>
 *   <li>에이전트 필터 설정</li>
 *   <li>Mock 서버 설정 (테스트용)</li>
 *   <li>CSRF 설정 관리</li>
 *   <li>인증 성공/실패 핸들러 설정</li>
 * </ul>
 * 
 * <p>기본 URL 패턴: /nsso/**</p>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * http
 *     .securityMatcher("/nsso/**")
 *     .csrf(AbstractHttpConfigurer::disable)
 *     .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
 *     .with(NetsSsoAuthenticationConfigurer.ssoConfigurer(), sso -> sso
 *         .prefixUrl("/nsso")
 *         .successHandler(customSuccessHandler)
 *         .failureHandler(customFailureHandler)
 *     );
 * }</pre>
 * 
 * @see org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 * @see NetsSsoAuthenticationFilter
 * @see NetsSsoRefreshTokenFilter
 * 
 * @author tykim
 * @since 0.0.0
 */
public final class NetsSsoAuthenticationConfigurer<B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<NetsSsoAuthenticationConfigurer<B>, B> {
    private final Log log = LogFactory.getLog(getClass());

    private String prefixPath = "/nsso";
    private boolean ignoreCsrf = false;

    private RequestMatcher endpointsMatcher;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    private LogoutSuccessHandler logoutSuccessHandler;

    private final NetsSsoAgentFilterConfigurer<B> agentFilterConfigurer = new NetsSsoAgentFilterConfigurer<>();
    private NetsSsoMockServerConfigurer<B> mockServerConfigurer;

    private RequestMatcher loginProcessRequestMatcher;
    private RequestMatcher logoutProcessRequestMatcher;
    private RequestMatcher requestTokenRequestMatcher;

    /**
     * NSSO 인증 설정 컨피규러의 새 인스턴스를 생성합니다.
     * 
     * @param <T> HttpSecurityBuilder 타입
     * @return 새로운 NetsSsoAuthenticationConfigurer 인스턴스
     */
    public static <T extends HttpSecurityBuilder<T>> NetsSsoAuthenticationConfigurer<T> ssoConfigurer() {
        return new NetsSsoAuthenticationConfigurer<>();
    }

    /**
     * {@link SecurityFilterChain} 생성을 위한 기본 설정을 적용
     * <pre>
     *     {@code
     *             NetsSsoAuthenticationConfigurer<HttpSecurity> configurer = ssoConfigurer();
     *             http
     *                 .securityMatcher(configurer.getEndpointsMatcher())
     *                 .csrf(AbstractHttpConfigurer::disable)
     *                 .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
     *                 .sessionManagement(session -> session
     *                         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
     *                 )
     *                 .with(configurer, sso -> sso
     *                         .mockServer(server -> server
     *                                 .profiles("nsso-mock")
     *                         )
     *                 );
     *     }
     * </pre>
     * 
     * @param http HttpSecurity 인스턴스
     * @return 설정된 NetsSsoAuthenticationConfigurer
     * @throws Exception 설정 중 오류 발생 시
     */
    public static NetsSsoAuthenticationConfigurer<HttpSecurity> withDefaults(HttpSecurity http) throws Exception {
        NetsSsoAuthenticationConfigurer<HttpSecurity> configurer = ssoConfigurer();

        http
                .securityMatcher(configurer.getEndpointsMatcher())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .with(configurer, sso -> sso
                        .mockServer(server -> server
                                .profiles("nsso-mock")
                        )
                );


        return configurer;
    }

    /**
     * 엔드포인트 매처를 반환한다.
     * 
     * @return 모든 NSSO 엔드포인트를 매칭하는 RequestMatcher
     */
    public RequestMatcher getEndpointsMatcher() {
        return (request) -> this.endpointsMatcher.matches(request);
    }

    /**
     * CSRF 보호를 무시할지 설정한다.
     * 
     * @param ignoreCsrf CSRF 보호 무시 여부
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> ignoreCsrf(boolean ignoreCsrf) {
        this.ignoreCsrf = true;
        return this;
    }

    /**
     * NSSO 인증 엔드포인트의 URL 접두사를 설정합니다.
     * 기본값은 "/nsso"입니다.
     *
     * @param prefixUrl NSSO 인증 엔드포인트의 URL 접두사
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> prefixUrl(String prefixUrl) {
        this.prefixPath = prefixUrl;
        return this;
    }

    /**
     * 인증 성공 핸들러를 설정한다.
     * 
     * @param successHandler 인증 성공 핸들러
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> successHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler must not be null");
        this.successHandler = successHandler;
        return this;
    }

    /**
     * 인증 실패 핸들러를 설정한다.
     * 
     * @param failureHandler 인증 실패 핸들러
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> failureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler must not be null");
        this.failureHandler = failureHandler;
        return this;
    }

    /**
     * 로그아웃 성공 핸들러를 설정한다.
     *
     * @param logoutSuccessHandler 인증 실패 핸들러
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> logoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler must not be null");
        this.logoutSuccessHandler = logoutSuccessHandler;
        return this;
    }


    /**
     * Mock 서버를 커스터마이징한다.
     * 
     * @param customizer Mock 서버 커스터마이저
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> mockServer(Customizer<NetsSsoMockServerConfigurer<B>> customizer) {
        Assert.notNull(customizer, "Customizer cannot be null");
        this.mockServerConfigurer = Objects.requireNonNullElseGet(this.mockServerConfigurer, NetsSsoMockServerConfigurer::new);
        customizer.customize(this.mockServerConfigurer);
        return this;
    }

    @Override
    public void init(B http) throws Exception {
        registerDefaultCsrfOverride(http);

        this.agentFilterConfigurer.init(http);
    }

    @Override
    public void configure(B http) throws Exception {
        if (this.mockServerConfigurer != null) {
            this.mockServerConfigurer.setPrefixPath(this.prefixPath);
            this.mockServerConfigurer.configure(http);
        }

        this.agentFilterConfigurer.setPrefixPath(this.prefixPath);
        this.agentFilterConfigurer.configure(http);

        configureAuthenticationFilter(http);
        configureLogoutFilter(http);
        configureRefreshTokenFilter(http);

        configureEndpointsMatcher();
    }

    private void configureAuthenticationFilter(B http) {
        String url = normalizePath(this.prefixPath, "/login");
        this.loginProcessRequestMatcher = antMatcher(HttpMethod.POST, url);
        var filter = new NetsSsoAuthenticationFilter(loginProcessRequestMatcher);
        configureAuthenticationProcessingFilter(http, filter);
    }

    private void configureLogoutFilter(B http) {
        String url = normalizePath(this.prefixPath, "/logout");
        this.logoutProcessRequestMatcher = antMatcher(url);
        var filter = new NetsSsoLogoutFilter(logoutSuccessHandler, new NetsSsoLogoutHandler());
        filter.setLogoutRequestMatcher(logoutProcessRequestMatcher);
    }


    private void configureRefreshTokenFilter(B http) {
        String url = normalizePath(this.prefixPath, "/refresh_token");
        this.requestTokenRequestMatcher = antMatcher(HttpMethod.POST, url);
        var filter = new NetsSsoRefreshTokenFilter(requestTokenRequestMatcher);
        configureAuthenticationProcessingFilter(http, filter);
    }

    private void configureAuthenticationProcessingFilter(B http, AbstractAuthenticationProcessingFilter filter) {
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(getSuccessHandler(http));
        filter.setAuthenticationFailureHandler(getFailureHandler(http));
        if (this.authenticationDetailsSource != null) {
            filter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        filter.setSecurityContextRepository(getSecurityContextRepository(http));
        filter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        http.addFilterBefore(postProcess(filter), UsernamePasswordAuthenticationFilter.class);
    }

    private void configureEndpointsMatcher() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        requestMatchers.add(this.loginProcessRequestMatcher);
        requestMatchers.add(this.logoutProcessRequestMatcher);
        requestMatchers.add(this.requestTokenRequestMatcher);
        requestMatchers.add(this.agentFilterConfigurer.getRequestMatcher());

        if (this.mockServerConfigurer != null) {
            requestMatchers.add(this.mockServerConfigurer.getRequestMatcher());
        }

        this.endpointsMatcher = new OrRequestMatcher(requestMatchers);

        log.debug("NetsSsoAuthentication endpoints:  " +  this.endpointsMatcher);
    }

    @SuppressWarnings("unchecked")
    private void registerDefaultCsrfOverride(B http) {
        if (ignoreCsrf) {
            CsrfConfigurer<B> csrf = http.getConfigurer(CsrfConfigurer.class);
            if (csrf != null) {
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(this.prefixPath + "/**", "POST");
                csrf.ignoringRequestMatchers(matcher);
            }
        }
    }

    /**
     * 인증 상세 정보 소스를 설정한다.
     * 기본값은 WebAuthenticationDetailsSource다.
     * 
     * @param authenticationDetailsSource 인증 상세 정보 소스
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoAuthenticationConfigurer<B> authenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this;
    }

    private SecurityContextRepository getSecurityContextRepository(B http) {
        var repository = http.getSharedObject(SecurityContextRepository.class);
        return Objects.requireNonNullElseGet(repository, HttpSessionSecurityContextRepository::new);
    }

    private AuthenticationSuccessHandler getSuccessHandler(B builder) {
        if (this.successHandler == null) {
            this.successHandler = new SimpleUrlAuthenticationSuccessHandler();
        }
        return this.successHandler;
    }

    private AuthenticationFailureHandler getFailureHandler(B builder) {
        if (this.failureHandler == null) {
            this.failureHandler = new SimpleUrlAuthenticationFailureHandler("/login?error");
        }
        return this.failureHandler;
    }
}
