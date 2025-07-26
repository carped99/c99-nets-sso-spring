package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NSSO 에이전트 2FA(2-Factor Authentication) 서비스 인터페이스
 * 
 * <p>이 인터페이스는 NSSO 에이전트의 2단계 인증 요청을 처리하는 서비스를 정의합니다.
 * 사용자의 2FA 인증 코드 검증 및 관리 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>2FA 인증 코드 검증</li>
 *   <li>2FA 설정 관리</li>
 *   <li>2FA 상태 확인</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAgentTfaService implements NetsSsoAgentTfaService {
 *     
 *     @Override
 *     public String process(HttpServletRequest request, HttpServletResponse response) {
 *         // 2FA 처리 로직 구현
 *         return "{\"result\": true, \"tfaValid\": true}";
 *     }
 * }
 * }</pre>
 * 
 * @author tykim
 * @since 0.0.0
 */
@FunctionalInterface
public interface NetsSsoAgentTfaService {
    /**
     * 2FA 요청을 처리합니다.
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 처리 결과 JSON 문자열
     */
    String process(HttpServletRequest request, HttpServletResponse response);
}
