package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NSSO 에이전트 키 서비스 인터페이스
 * 
 * <p>이 인터페이스는 NSSO 에이전트의 키 관리 요청을 처리하는 서비스를 정의합니다.
 * 암호화 키, 인증 키 등의 생성, 조회, 갱신 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>키 생성 및 조회</li>
 *   <li>키 갱신</li>
 *   <li>키 유효성 검증</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentKeyService implements NetsSsoAgentKeyService {
 *     
 *     @Override
 *     public String process(HttpServletRequest request, HttpServletResponse response) {
 *         // 키 관리 로직 구현
 *         return "{\"result\": true, \"key\": \"...\"}";
 *     }
 * }
 * }</pre>
 * 
 * @author carped99
 * @since 0.0.1
 */
@FunctionalInterface
public interface NetsSsoAgentKeyService {
    /**
     * 키 관리 요청을 처리합니다.
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 처리 결과 JSON 문자열
     */
    String process(HttpServletRequest request, HttpServletResponse response);
}