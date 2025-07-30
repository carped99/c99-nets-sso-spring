package io.github.carped99.nsso.configure;

import io.github.carped99.nsso.NetsSsoAgentCheckService;
import io.github.carped99.nsso.NetsSsoAgentConfigService;
import io.github.carped99.nsso.NetsSsoAuthenticationService;
import io.github.carped99.nsso.mock.NetsSsoAuthenticationMockService;
import io.github.carped99.nsso.mock.NetsSsoMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.Map;

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
 *             .profiles("nsso-mock")
 *             .checkCustomizer(data -> data.put("customField", "value"))
 *             .configCustomizer(data -> data.put("configField", "value"))
 *         )
 *     );
 * }</pre>
 *
 * @author carped99
 * @see io.github.carped99.nsso.mock.NetsSsoMockServer
 * @see io.github.carped99.nsso.mock.NetsSsoAuthenticationMockService
 * @see org.springframework.security.config.annotation.SecurityConfigurerAdapter
 * @since 0.0.1
 */
public class NetsSsoMockServerConfigurer<B extends HttpSecurityBuilder<B>> extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {
    private final Log log = LogFactory.getLog(getClass());
    private String[] profiles;
    private String prefixPath;
    private NetsSsoMockServer mockServer;

    @Nullable
    private Customizer<Map<String, Object>> checkCustomizer;
    @Nullable
    private Customizer<Map<String, Object>> configCustomizer;

    @Override
    public void configure(B builder) throws Exception {
        Environment environment = builder.getSharedObject(ApplicationContext.class).getEnvironment();
        if (!isEnabled(environment)) {
            log.debug("NSSO Mock Server is not enabled");
            return;
        }

        String prefixUrl = prefixPath + "/server";
        this.mockServer = NetsSsoConfigurerUtils.getMockServer(builder)
                .setPrefixUrl(prefixUrl);

        UserDetailsService userDetailsService = NetsSsoConfigurerUtils.getUserDetailsService(builder);
        if (userDetailsService != null) {
            this.mockServer.setUserDetailsService(userDetailsService);
        }
        this.mockServer.configure(builder);

        // Bean 등록
        NetsSsoConfigurerUtils.getBean(builder, NetsSsoAuthenticationService.class, NetsSsoAuthenticationMockService::new);
        NetsSsoConfigurerUtils.getBean(builder, NetsSsoAgentCheckService.class, () -> mockServer.getAgentCheckService(this.checkCustomizer));
        NetsSsoConfigurerUtils.getBean(builder, NetsSsoAgentConfigService.class, () -> mockServer.getAgentConfigService(this.configCustomizer));
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

    /**
     * 체크 서비스 응답 데이터 커스터마이저를 설정합니다.
     *
     * @param customizer 체크 서비스 커스터마이저
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     * @throws IllegalArgumentException customizer가 null인 경우
     */
    public NetsSsoMockServerConfigurer<B> checkCustomizer(Customizer<Map<String, Object>> customizer) {
        Assert.notNull(customizer, "Customizer must not be null");
        this.checkCustomizer = customizer;
        return this;
    }

    /**
     * 설정 서비스 응답 데이터 커스터마이저를 설정합니다.
     *
     * @param customizer 설정 서비스 커스터마이저
     * @return 현재 컨피규러 인스턴스 (메서드 체이닝 지원)
     * @throws IllegalArgumentException customizer가 null인 경우
     */
    public NetsSsoMockServerConfigurer<B> configCustomizer(Customizer<Map<String, Object>> customizer) {
        Assert.notNull(customizer, "Customizer must not be null");
        this.configCustomizer = customizer;
        return this;
    }

    private boolean isEnabled(Environment environment) {
        if (profiles == null || profiles.length == 0) {
            return false;
        }
        return environment.matchesProfiles(profiles);
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
}