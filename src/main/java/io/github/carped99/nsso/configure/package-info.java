/**
 * NSSO Spring Security 설정 컴포넌트
 *
 * <p>이 패키지는 NSSO와 Spring Security의 통합을 위한 설정 클래스들을 포함합니다.
 * Spring Security의 HttpSecurity 빌더를 확장하여 NSSO 인증을 쉽게 구성할 수 있도록 합니다.</p>
 *
 * <p>주요 컴포넌트:</p>
 * <ul>
 *   <li>{@link io.github.carped99.nsso.configure.NetsSsoAuthenticationConfigurer} - NSSO 인증 설정의 메인 컨피규러</li>
 *   <li>{@link io.github.carped99.nsso.configure.NetsSsoAgentFilterConfigurer} - NSSO 에이전트 필터 설정</li>
 *   <li>{@link io.github.carped99.nsso.configure.NetsSsoMockServerConfigurer} - Mock 서버 설정 (테스트용)</li>
 *   <li>{@link io.github.carped99.nsso.configure.NetsSsoConfigurerUtils} - 설정 유틸리티 클래스</li>
 * </ul>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig {
 *
 *     @Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         http
 *             .securityMatcher("/nsso/**")
 *             .csrf(AbstractHttpConfigurer::disable)
 *             .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
 *             .with(NetsSsoAuthenticationConfigurer.ssoConfigurer(), sso -> sso
 *                 .prefixUrl("/nsso")
 *                 .successHandler(customSuccessHandler)
 *                 .failureHandler(customFailureHandler)
 *             );
 *         return http.build();
 *     }
 * }
 * }</pre>
 *
 * @author carped99
 * @see io.github.carped99.nsso.configure.NetsSsoAuthenticationConfigurer
 * @see io.github.carped99.nsso.configure.NetsSsoAgentFilterConfigurer
 * @see io.github.carped99.nsso.configure.NetsSsoMockServerConfigurer
 * @since 0.0.1
 */
@NonNullApi
package io.github.carped99.nsso.configure;

import org.springframework.lang.NonNullApi;