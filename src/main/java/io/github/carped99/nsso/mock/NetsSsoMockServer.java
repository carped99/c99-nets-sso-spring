package io.github.carped99.nsso.mock;

import io.github.carped99.nsso.NetsSsoUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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
     * Mock 로그인 경로
     */
    static final String LOGON_PATH = "/logonService";
    /**
     * Mock 로그아웃 경로
     */
    static final String LOGOFF_PATH = "/logoffService";
    /**
     * Mock 체크 경로
     */
    static final String CHECK_PATH = "/checkService";

    private String prefixPath;
    private UserDetailsService userDetailsService;
    private NetsSsoServerLogonFilter logonFilter;
    private NetsSsoServerLogoutFilter logoffFilter;
    private NetsSsoServerCheckFilter checkFilter;

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
        RequestMatcher logonRequestMatcher = antMatcher(HttpMethod.POST, NetsSsoUtils.normalizePath(prefixPath, LOGON_PATH));
        RequestMatcher logoffRequestMatcher = antMatcher(HttpMethod.POST, NetsSsoUtils.normalizePath(prefixPath, LOGOFF_PATH));
        RequestMatcher checkRequestMatcher = antMatcher(HttpMethod.POST, NetsSsoUtils.normalizePath(prefixPath, CHECK_PATH));

        this.logonFilter = new NetsSsoServerLogonFilter(logonRequestMatcher, this.userDetailsService);
        http.addFilterAfter(logonFilter, CsrfFilter.class);

        this.logoffFilter = new NetsSsoServerLogoutFilter(logoffRequestMatcher);
        http.addFilterAfter(logoffFilter, CsrfFilter.class);

        this.checkFilter = new NetsSsoServerCheckFilter(checkRequestMatcher);
        http.addFilterAfter(checkFilter, CsrfFilter.class);
    }

    /**
     * Mock 서버의 RequestMatcher를 반환합니다.
     *
     * @return 모든 Mock 엔드포인트를 매칭하는 RequestMatcher
     */
    public RequestMatcher getRequestMatcher() {
        return new OrRequestMatcher(
                logonFilter.getRequestMatcher(),
                checkFilter.getRequestMatcher(),
                logoffFilter.getRequestMatcher()
        );
    }

    /**
     * URL 접두사를 설정합니다.
     *
     * @param prefixPath URL 접두사
     * @return 현재 Mock 서버 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoMockServer setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
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
