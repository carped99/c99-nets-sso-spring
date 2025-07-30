package io.github.carped99.nsso.mock;

import io.github.carped99.nsso.NetsSsoAgentCheckService;
import io.github.carped99.nsso.NetsSsoAgentConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * 개발 환경에서 사용할 NSSO Mock 서버
 *
 * <p>이 클래스는 개발 및 테스트 환경에서 실제 NSSO 서버 없이도 SSO 기능을 테스트할 수 있도록
 * Mock 서버를 제공합니다. 로그인, 로그아웃, 체크 등의 기능을 시뮬레이션합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>Mock 로그인 필터 (/logon)</li>
 *   <li>Mock 로그아웃 필터 (/logout)</li>
 *   <li>Mock 체크 필터 (/check)</li>
 *   <li>Mock 에이전트 설정 서비스</li>
 *   <li>Mock 에이전트 체크 서비스</li>
 * </ul>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * NetsSsoMockServer mockServer = new NetsSsoMockServer()
 *     .setPrefixUrl("/nsso")
 *     .setUserDetailsService(userDetailsService);
 * mockServer.configure(httpSecurity);
 * }</pre>
 *
 * <p>프로파일 기반 활성화:</p>
 * <pre>{@code
 * @Profile("nsso-mock")
 * @Bean
 * public NetsSsoMockServer mockServer() {
 *     return new NetsSsoMockServer();
 * }
 * }</pre>
 *
 * @author carped99
 * @see io.github.carped99.nsso.mock.NetsSsoServerLogonFilter
 * @see io.github.carped99.nsso.mock.NetsSsoServerLogoutFilter
 * @see io.github.carped99.nsso.mock.NetsSsoServerCheckFilter
 * @since 0.0.1
 */
public class NetsSsoMockServer {
    /**
     * 로그 인스턴스
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Mock 로그인 경로
     */
    static final String LOGON_PATH = "/logonService";
    /**
     * Mock 로그아웃 경로
     */
    static final String LOGOUT_PATH = "/logoutService";
    /**
     * Mock 체크 경로
     */
    static final String CHECK_PATH = "/checkService";

    private String prefixUrl;
    private UserDetailsService userDetailsService;
    private RequestMatcher requestMatcher;

    /**
     * Mock 서버를 Spring Security 설정에 추가합니다.
     *
     * <p>설정 과정:</p>
     * <ol>
     *   <li>로그인, 로그아웃, 체크 요청 매처 생성</li>
     *   <li>각각의 Mock 필터 생성 및 설정</li>
     *   <li>CSRF 필터 이후에 필터 추가</li>
     *   <li>전체 RequestMatcher 생성</li>
     * </ol>
     *
     * @param http HttpSecurity 빌더
     */
    public void configure(HttpSecurityBuilder<?> http) {
        RequestMatcher logonRequestMatcher = createRequestMatcher(prefixUrl, LOGON_PATH);
        RequestMatcher logoutRequestMatcher = createRequestMatcher(prefixUrl, LOGOUT_PATH);
        RequestMatcher checkRequestMatcher = createRequestMatcher(prefixUrl, CHECK_PATH);

        var logonFilter = new NetsSsoServerLogonFilter(logonRequestMatcher, this.userDetailsService);
        http.addFilterAfter(logonFilter, CsrfFilter.class);

        var logoutFilter = new NetsSsoServerLogoutFilter(logoutRequestMatcher);
        http.addFilterAfter(logoutFilter, CsrfFilter.class);

        var checkFilter = new NetsSsoServerCheckFilter(checkRequestMatcher);
        http.addFilterAfter(checkFilter, CsrfFilter.class);

        this.requestMatcher = new OrRequestMatcher(List.of(logonRequestMatcher, logoutRequestMatcher, checkRequestMatcher));

        log.info("NetsSsoMockServer initialized: " + this.requestMatcher);
    }

    /**
     * 접두사와 접미사를 결합하여 RequestMatcher를 생성합니다.
     *
     * @param prefix URL 접두사
     * @param suffix URL 접미사
     * @return POST 요청을 매칭하는 RequestMatcher
     */
    private RequestMatcher createRequestMatcher(String prefix, String suffix) {
        var path = UriComponentsBuilder.fromPath(prefix)
                .path(suffix)
                .build()
                .normalize()
                .getPath();

        assert path != null;
        return AntPathRequestMatcher.antMatcher(HttpMethod.POST, path);
    }

    /**
     * Mock 에이전트 설정 서비스를 생성합니다.
     *
     * @param customizer 설정 데이터 커스터마이저
     * @return Mock 에이전트 설정 서비스
     */
    public NetsSsoAgentConfigService getAgentConfigService(@Nullable Customizer<Map<String, Object>> customizer) {
        return new NetsSsoAgentConfigMockService(prefixUrl, customizer);
    }

    /**
     * Mock 에이전트 체크 서비스를 생성합니다.
     *
     * @param customizer 체크 응답 데이터 커스터마이저
     * @return Mock 에이전트 체크 서비스
     */
    public NetsSsoAgentCheckService getAgentCheckService(@Nullable Customizer<Map<String, Object>> customizer) {
        return new NetsSsoAgentCheckMockService(customizer);
    }

    /**
     * Mock 서버의 RequestMatcher를 반환합니다.
     *
     * @return 모든 Mock 엔드포인트를 매칭하는 RequestMatcher
     */
    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    /**
     * URL 접두사를 설정합니다.
     *
     * @param prefixUrl URL 접두사
     * @return 현재 Mock 서버 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoMockServer setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
        return this;
    }

    /**
     * UserDetailsService를 설정합니다.
     *
     * @param userDetailsService 사용자 정보 서비스
     * @return 현재 Mock 서버 인스턴스 (메서드 체이닝 지원)
     * @throws IllegalArgumentException userDetailsService가 null인 경우
     */
    public NetsSsoMockServer setUserDetailsService(UserDetailsService userDetailsService) {
        Assert.notNull(userDetailsService, "UserDetailsService must not be null");
        this.userDetailsService = userDetailsService;
        return this;
    }
}
