/**
 * NSSO Mock 서버 및 테스트 컴포넌트
 * 
 * <p>이 패키지는 NSSO 에이전트의 Mock 서버 및 테스트를 위한 컴포넌트들을 포함합니다.
 * 개발 및 테스트 환경에서 실제 NSSO 서버 없이도 SSO 인증을 시뮬레이션할 수 있습니다.</p>
 * 
 * <p>주요 컴포넌트:</p>
 * <ul>
 *   <li>{@link NetsSsoMockServer} - NSSO Mock 서버</li>
 *   <li>{@link NetsSsoAgentCheckMockService} - 에이전트 체크 Mock 서비스</li>
 *   <li>{@link NetsSsoAgentConfigMockService} - 에이전트 설정 Mock 서비스</li>
 *   <li>{@link NetsSsoAuthenticationMockService} - 인증 Mock 서비스</li>
 *   <li>{@link NetsSsoServerCheckFilter} - 서버 체크 필터</li>
 *   <li>{@link NetsSsoServerLogonFilter} - 서버 로그온 필터</li>
 *   <li>{@link NetsSsoServerLogoutFilter} - 서버 로그아웃 필터</li>
 * </ul>
 * 
 * <p>Mock 서버는 다음과 같은 상황에서 유용합니다:</p>
 * <ul>
 *   <li>개발 환경에서 NSSO 서버가 없는 경우</li>
 *   <li>단위 테스트 및 통합 테스트</li>
 *   <li>SSO 인증 플로우 시뮬레이션</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Configuration
 * @Profile("nsso-mock")
 * public class MockConfig {
 *     
 *     @Bean
 *     public NetsSsoMockServer mockServer() {
 *         return new NetsSsoMockServer();
 *     }
 * }
 * }</pre>
 * 
 * @see io.github.carped99.nsso.mock.NetsSsoMockServer
 * @see io.github.carped99.nsso.mock.NetsSsoAgentCheckMockService
 * @see io.github.carped99.nsso.mock.NetsSsoAuthenticationMockService
 * 
 * @author carped99
 * @since 0.0.1
 */
@NonNullApi
package io.github.carped99.nsso.mock;

import org.springframework.lang.NonNullApi;