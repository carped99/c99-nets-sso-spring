package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NSSO 에이전트 중복 로그인 서비스 인터페이스
 * 
 * <p>이 인터페이스는 NSSO 에이전트의 중복 로그인 요청을 처리하는 서비스를 정의합니다.
 * 동일한 사용자가 여러 세션에서 로그인하는 상황을 관리하는 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>중복 로그인 감지</li>
 *   <li>기존 세션 관리</li>
 *   <li>중복 로그인 정책 적용</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentDuplicateService implements NetsSsoAgentDuplicateService {
 *     
 *     @Override
 *     public String process(HttpServletRequest request, HttpServletResponse response) {
 *         // 중복 로그인 처리 로직 구현
 *         return "{\"result\": true, \"duplicate\": false}";
 *     }
 * }
 * }</pre>
 * 
 * @author carped99
 * @since 0.0.1
 */
@FunctionalInterface
public interface NetsSsoAgentDuplicateService {
    /**
     * 중복 로그인 요청을 처리합니다.
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 처리 결과 JSON 문자열
     */
    String process(HttpServletRequest request, HttpServletResponse response);
}
