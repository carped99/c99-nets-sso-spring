/**
 * NSSO 인터페이스 구현체
 * 
 * <p>이 패키지는 NSSO 인터페이스들의 기본 구현체들을 포함합니다.
 * 실제 NSSO 에이전트와의 통신 및 인증 처리를 담당합니다.</p>
 * 
 * <p>주요 구현체:</p>
 * <ul>
 *   <li>{@link NetsSsoAuthenticationServiceImpl} - NSSO 인증 서비스 구현체</li>
 *   <li>{@link NetsSsoUserImpl} - NSSO 사용자 정보 구현체</li>
 *   <li>{@link NetsSsoAgentCheckServiceImpl} - 에이전트 체크 서비스 구현체</li>
 *   <li>{@link NetsSsoAgentConfigServiceImpl} - 에이전트 설정 서비스 구현체</li>
 *   <li>{@link NetsSsoAgentDuplicateServiceImpl} - 에이전트 중복 로그인 서비스 구현체</li>
 *   <li>{@link NetsSsoAgentKeyServiceImpl} - 에이전트 키 서비스 구현체</li>
 *   <li>{@link NetsSsoAgentTfaServiceImpl} - 에이전트 2FA 서비스 구현체</li>
 * </ul>
 * 
 * <p>이 구현체들은 NSSO 에이전트의 다양한 기능들을 처리하며,
 * Spring Security와의 통합을 위한 어댑터 역할을 합니다.</p>
 * 
 * @see io.github.carped99.nsso.NetsSsoAuthenticationService
 * @see io.github.carped99.nsso.NetsSsoUser
 * @see io.github.carped99.nsso.NetsSsoAgentCheckService
 * @see io.github.carped99.nsso.NetsSsoAgentConfigService
 * @see io.github.carped99.nsso.NetsSsoAgentDuplicateService
 * @see io.github.carped99.nsso.NetsSsoAgentKeyService
 * @see io.github.carped99.nsso.NetsSsoAgentTfaService
 * 
 * @author carped99
 * @since 0.0.1
 */
@NonNullApi
package io.github.carped99.nsso.impl;

import org.springframework.lang.NonNullApi;