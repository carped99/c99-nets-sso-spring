/**
 * NSSO (Nets Single Sign-On) Spring Security 통합 라이브러리
 *
 * <p>이 패키지는 NSSO 에이전트와 Spring Security를 통합하여
 * 단일 로그온(SSO) 인증을 제공하는 핵심 컴포넌트들을 포함합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>NSSO 인증 토큰 처리</li>
 *   <li>SSO 사용자 정보 관리</li>
 *   <li>인증 서비스 제공</li>
 *   <li>에이전트 필터링</li>
 *   <li>액세스 토큰 및 리프레시 토큰 처리</li>
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
 *             .with(NetsSsoAuthenticationConfigurer.withDefaults(http), sso -> {});
 *         return http.build();
 *     }
 * }
 * }</pre>
 *
 * @author carped99
 * @see io.github.carped99.nsso.NetsSsoAuthentication
 * @see io.github.carped99.nsso.NetsSsoUser
 * @see io.github.carped99.nsso.NetsSsoAuthenticationService
 * @see io.github.carped99.nsso.configure.NetsSsoAuthenticationConfigurer
 * @since 0.0.1
 */
@NonNullApi
package io.github.carped99.nsso;

import org.springframework.lang.NonNullApi;