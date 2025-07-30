package io.github.carped99.nsso.configure;

import io.github.carped99.nsso.*;
import io.github.carped99.nsso.impl.*;
import io.github.carped99.nsso.mock.NetsSsoMockServer;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * NSSO 설정 유틸리티 클래스
 *
 * <p>이 클래스는 NSSO 설정 과정에서 사용되는 유틸리티 메서드들을 제공합니다.
 * 경로 구성, 빈 조회, 서비스 인스턴스 생성 등의 기능을 포함합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>URL 경로 구성</li>
 *   <li>Spring Security 빌더에서 빈 조회</li>
 *   <li>에이전트 서비스 인스턴스 생성</li>
 *   <li>Mock 서버 인스턴스 생성</li>
 * </ul>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * String path = NetsSsoConfigurerUtils.buildPath("/nsso", "/check");
 * NetsSsoAgentCheckService service = NetsSsoConfigurerUtils.getAgentCheckService(httpSecurity);
 * }</pre>
 *
 * @author carped99
 * @see NetsSsoAgentCheckService
 * @see NetsSsoAgentConfigService
 * @see NetsSsoAgentDuplicateService
 * @see NetsSsoAgentTfaService
 * @see NetsSsoAgentKeyService
 * @since 0.0.1
 */
final class NetsSsoConfigurerUtils {
    /**
     * 유틸리티 클래스이므로 인스턴스화를 방지합니다.
     */
    private NetsSsoConfigurerUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }



    /**
     * HttpSecurity 빌더에서 에이전트 체크 서비스를 조회하거나 생성합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return 에이전트 체크 서비스 인스턴스
     */
    public static NetsSsoAgentCheckService getAgentCheckService(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, NetsSsoAgentCheckService.class, NetsSsoAgentCheckServiceImpl::new);
    }

    /**
     * HttpSecurity 빌더에서 에이전트 설정 서비스를 조회하거나 생성합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return 에이전트 설정 서비스 인스턴스
     */
    public static NetsSsoAgentConfigService getAgentConfigService(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, NetsSsoAgentConfigService.class, NetsSsoAgentConfigServiceImpl::new);
    }

    /**
     * HttpSecurity 빌더에서 에이전트 중복 로그인 서비스를 조회하거나 생성합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return 에이전트 중복 로그인 서비스 인스턴스
     */
    public static NetsSsoAgentDuplicateService getAgentDuplicateService(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, NetsSsoAgentDuplicateService.class, NetsSsoAgentDuplicateServiceImpl::new);
    }

    /**
     * HttpSecurity 빌더에서 에이전트 키 서비스를 조회하거나 생성합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return 에이전트 키 서비스 인스턴스
     */
    public static NetsSsoAgentKeyService getAgentKeyService(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, NetsSsoAgentKeyService.class, NetsSsoAgentKeyServiceImpl::new);
    }

    /**
     * HttpSecurity 빌더에서 에이전트 2FA 서비스를 조회하거나 생성합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return 에이전트 2FA 서비스 인스턴스
     */
    public static NetsSsoAgentTfaService getAgentTfaService(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, NetsSsoAgentTfaService.class, NetsSsoAgentTfaServiceImpl::new);
    }

    /**
     * HttpSecurity 빌더에서 Mock 서버를 조회하거나 생성합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return Mock 서버 인스턴스
     */
    public static NetsSsoMockServer getMockServer(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, NetsSsoMockServer.class, NetsSsoMockServer::new);
    }

    /**
     * HttpSecurity 빌더에서 UserDetailsService를 조회합니다.
     *
     * @param httpSecurity HttpSecurity 빌더
     * @return UserDetailsService 인스턴스 (없으면 null)
     */
    @Nullable
    public static UserDetailsService getUserDetailsService(HttpSecurityBuilder<?> httpSecurity) {
        return getBean(httpSecurity, UserDetailsService.class);
    }

    /**
     * HttpSecurity 빌더에서 지정된 타입의 빈을 조회합니다.
     *
     * <p>먼저 SharedObject에서 조회하고, 없으면 ApplicationContext에서 조회합니다.</p>
     *
     * @param http  HttpSecurity 빌더
     * @param clazz 조회할 빈 타입
     * @param <C>   빈 타입
     * @return 빈 인스턴스 (없으면 null)
     */
    @Nullable
    private static <C> C getBean(HttpSecurityBuilder<?> http, Class<C> clazz) {
        return Optional.ofNullable(http.getSharedObject(clazz)).orElseGet(() -> {
            var provider = http.getSharedObject(ApplicationContext.class).getBeanProvider(clazz);
            return provider.getIfUnique();
        });
    }

    /**
     * HttpSecurity 빌더에서 지정된 타입의 빈을 조회하거나 생성합니다.
     *
     * <p>먼저 SharedObject에서 조회하고, 없으면 ApplicationContext에서 조회하며,
     * 둘 다 없으면 supplier를 사용하여 새 인스턴스를 생성합니다.</p>
     *
     * @param http     HttpSecurity 빌더
     * @param clazz    조회할 빈 타입
     * @param supplier 빈 생성 공급자
     * @param <C>      빈 타입
     * @return 빈 인스턴스
     */
    public static <C> C getBean(HttpSecurityBuilder<?> http, Class<C> clazz, Supplier<C> supplier) {
        return Optional.ofNullable(http.getSharedObject(clazz)).orElseGet(() -> {
            var provider = http.getSharedObject(ApplicationContext.class).getBeanProvider(clazz);
            var bean = provider.getIfUnique(supplier);
            http.setSharedObject(clazz, bean);
            return bean;
        });
    }
}
