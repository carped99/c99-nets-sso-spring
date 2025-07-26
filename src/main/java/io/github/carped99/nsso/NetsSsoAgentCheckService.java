package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NSSO 에이전트 체크 서비스 인터페이스
 * 
 * <p>이 인터페이스는 NSSO 에이전트의 상태 확인 요청을 처리하는 서비스를 정의합니다.
 * 에이전트의 동작 상태, 연결 상태 등을 확인하는 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>에이전트 상태 확인</li>
 *   <li>연결 상태 검증</li>
 *   <li>에이전트 설정 유효성 검사</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentCheckService implements NetsSsoAgentCheckService {
 *     
 *     @Override
 *     public String process(HttpServletRequest request, HttpServletResponse response) {
 *         // 에이전트 체크 로직 구현
 *         return "{\"result\": true, \"status\": \"OK\"}";
 *     }
 * }
 * }</pre>
 * 
 * @author tykim
 * @since 0.0.0
 */
@FunctionalInterface
public interface NetsSsoAgentCheckService {
    /**
     * 에이전트 체크 요청을 처리합니다.
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 처리 결과 JSON 문자열
     */
    String process(HttpServletRequest request, HttpServletResponse response);
}
