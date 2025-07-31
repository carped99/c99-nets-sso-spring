package io.github.carped99.nsso.configure;

import io.github.carped99.nsso.NetsSsoAgentService;
import io.github.carped99.nsso.NetsSsoAuthenticationService;
import io.github.carped99.nsso.mock.NetsSsoMockAgentService;
import io.github.carped99.nsso.mock.NetsSsoMockAuthenticationService;
import io.github.carped99.nsso.mock.NetsSsoMockAuthenticationSuccessHandler;
import io.github.carped99.nsso.mock.NetsSsoMockServer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import static io.github.carped99.nsso.NetsSsoUtils.normalizePath;

/**
 * NSSO Mock 서버 설정 컨피규러
 *
 * <p>이 클래스는 NSSO Mock 서버를 Spring Security 설정에 추가하는 컨피규러입니다.
 * 개발 및 테스트 환경에서 실제 NSSO 서버 없이도 SSO 기능을 테스트할 수 있도록 Mock 서버를 구성합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>프로파일 기반 Mock 서버 활성화</li>
 *   <li>Mock 인증 서비스 자동 등록</li>
 *   <li>Mock 에이전트 서비스 자동 등록</li>
 *   <li>커스터마이저를 통한 응답 데이터 커스터마이징</li>
 * </ul>
 *
 * <p>설정 과정:</p>
 * <ol>
 *   <li>프로파일 확인 및 활성화 여부 결정</li>
 *   <li>Mock 서버 인스턴스 생성 및 설정</li>
 *   <li>UserDetailsService 설정 (있는 경우)</li>
 *   <li>Mock 서버를 Spring Security에 추가</li>
 *   <li>Mock 서비스들을 Bean으로 등록</li>
 * </ol>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * http
 *     .with(NetsSsoAuthenticationConfigurer.ssoConfigurer(), sso -> sso
 *         .mockServer(server -> server
 *             .profiles("mocking-nsso")
 *             .checkCustomizer(data -> data.put("customField", "value"))
 *             .configCustomizer(data -> data.put("configField", "value"))
 *         )
 *     );
 * }</pre>
 *
 * @author carped99
 * @see io.github.carped99.nsso.mock.NetsSsoMockServer
 * @see NetsSsoMockAuthenticationService
 * @see org.springframework.security.config.annotation.SecurityConfigurerAdapter
 * @since 0.0.1
 */
public class NetsSsoMockServerConfigurer<B extends HttpSecurityBuilder<B>> extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {
    private boolean enabled = false;
    private String[] profiles;
    private String prefixPath;
    private NetsSsoMockServer mockServer;

    @Override
    public void configure(B builder) throws Exception {
        Environment environment = builder.getSharedObject(ApplicationContext.class).getEnvironment();
        this.enabled = isEnabled(environment);
        if (!this.enabled) {
            return;
        }

        String serverPath = normalizePath(prefixPath, "/server");

        this.mockServer = NetsSsoConfigurerUtils.getBean(builder, NetsSsoMockServer.class, NetsSsoMockServer::new)
                .setPrefixPath(serverPath);

        UserDetailsService userDetailsService = NetsSsoConfigurerUtils.getBean(builder, UserDetailsService.class);
        if (userDetailsService == null) {
            throw new IllegalStateException("UserDetailsService required");
        }
        this.mockServer.setUserDetailsService(userDetailsService);
        this.mockServer.configure(builder);

        // Bean 등록
        NetsSsoConfigurerUtils.getBean(builder, NetsSsoAuthenticationService.class, NetsSsoMockAuthenticationService::new);
        NetsSsoConfigurerUtils.getBean(builder, NetsSsoAgentService.class, () -> new NetsSsoMockAgentService(serverPath));
    }

    /**
     * 활성화할 프로파일을 설정한다.
     *
     * @param profiles 활성화할 프로파일 목록
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     */
    public NetsSsoMockServerConfigurer<B> profiles(String... profiles) {
        this.profiles = profiles;
        return this;
    }

    private boolean isEnabled(Environment environment) {
        if (profiles == null || profiles.length == 0) {
            return false;
        }
        return environment.matchesProfiles(profiles);
    }

    boolean isEnabled() {
        return this.enabled;
    }

    RequestMatcher getRequestMatcher() {
        if (this.mockServer == null) {
            return (request) -> false;
        }
        return this.mockServer.getRequestMatcher();
    }

    void setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    AuthenticationSuccessHandler decorateSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler must not be null");
        return new NetsSsoMockAuthenticationSuccessHandler(successHandler);
    }
}