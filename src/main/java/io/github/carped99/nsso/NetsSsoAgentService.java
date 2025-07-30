package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NSSO 에이전트 서비스 인터페이스
 *
 * <p>이 인터페이스는 NSSO 에이전트와의 상호작용을 정의합니다.
 * NSSO 에이전트의 인증 상태 확인, 설정 정보 반환, 중복 인증 정보 반환,
 * 공개 키 반환, 다중 인증(TFA) 정보 반환 등의 기능을 제공합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>NSSO 에이전트의 인증 상태 확인</li>
 *   <li>NSSO 에이전트의 설정 정보 반환</li>
 *   <li>NSSO 에이전트의 중복 인증 정보 반환</li>
 *   <li>NSSO 에이전트의 공개 키 반환</li>
 *   <li>NSSO 에이전트의 다중 인증(TFA) 정보 반환</li>
 * </ul>
 *
 * @author carped99
 * @since 0.0.1
 */
public interface NetsSsoAgentService {
    /**
     * NSSO 에이전트의 인증 상태를 확인합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증된 사용자 정보 JSON 문자열
     */
    String check(HttpServletRequest request, HttpServletResponse response);

    /**
     * NSSO 에이전트의 설정 정보를 반환합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return NSSO 에이전트 설정 정보 JSON 문자열
     */
    String config(HttpServletRequest request, HttpServletResponse response);

    /**
     * NSSO 에이전트의 중복 인증 정보를 반환합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 중복 인증 정보 JSON 문자열
     */
    String duplicate(HttpServletRequest request, HttpServletResponse response);

    /**
     * NSSO 에이전트의 공개 키를 반환합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return NSSO 에이전트 공개 키 JSON 문자열
     */
    String key(HttpServletRequest request, HttpServletResponse response);

    /**
     * NSSO 에이전트의 다중 인증(TFA) 정보를 반환합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 다중 인증 정보 JSON 문자열
     */
    String tfa(HttpServletRequest request, HttpServletResponse response);
}
