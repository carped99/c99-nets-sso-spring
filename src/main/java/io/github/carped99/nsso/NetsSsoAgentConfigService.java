package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NSSO 에이전트 설정 서비스 인터페이스
 * 
 * <p>이 인터페이스는 NSSO 에이전트의 설정 관리 요청을 처리하는 서비스를 정의합니다.
 * 에이전트의 설정 정보를 조회, 수정, 업데이트하는 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>에이전트 설정 조회</li>
 *   <li>설정 정보 업데이트</li>
 *   <li>설정 유효성 검증</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentConfigService implements NetsSsoAgentConfigService {
 *     
 *     @Override
 *     public String process(HttpServletRequest request, HttpServletResponse response) {
 *         // 에이전트 설정 처리 로직 구현
 *         return "{\"result\": true, \"config\": {...}}";
 *     }
 * }
 * }</pre>
 * 
 * @author carped99
 * @since 0.0.1
 */
@FunctionalInterface
public interface NetsSsoAgentConfigService {
    /**
     * 에이전트 설정 요청을 처리합니다.
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 처리 결과 JSON 문자열
     */
    String process(HttpServletRequest request, HttpServletResponse response);
}

