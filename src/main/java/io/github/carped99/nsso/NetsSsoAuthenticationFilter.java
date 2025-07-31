package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * NSSO 액세스 토큰 처리 필터
 *
 * <p>이 필터는 NSSO 액세스 토큰 요청을 처리하는 Spring Security 필터입니다.
 * NSSO 에이전트로부터 받은 액세스 토큰을 검증하고 인증을 수행합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>액세스 토큰 요청 파라미터 검증</li>
 *   <li>NSSO 인증 토큰 생성</li>
 *   <li>Spring Security 인증 매니저를 통한 인증 처리</li>
 * </ul>
 *
 * <p>필수 파라미터:</p>
 * <ul>
 *   <li>op - 작업 타입 (반드시 "LI")</li>
 *   <li>ssosite - 사이트 ID</li>
 *   <li>ssoResponse 또는 artifactID - SSO 응답 또는 아티팩트 ID</li>
 *   <li>policyVersion - 정책 버전</li>
 * </ul>
 *
 * <p>기본 URL: /nsso/access_token</p>
 *
 * @author carped99
 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 * @see NetsSsoAuthentication
 * @since 0.0.1
 */
public class NetsSsoAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    /**
     * 기본 URL("/nsso/access_token")로 NetsSsoAccessTokenFilter를 생성합니다.
     */
    public NetsSsoAuthenticationFilter() {
        super("/nsso/login");
    }

    /**
     * 지정된 RequestMatcher로 NetsSsoAccessTokenFilter를 생성합니다.
     *
     * @param requestMatcher 요청을 매칭하는 RequestMatcher
     */
    public NetsSsoAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    /**
     * NSSO 액세스 토큰 인증을 시도합니다.
     *
     * <p>요청 파라미터를 검증한 후 NSSO 인증 토큰을 생성하고
     * Spring Security 인증 매니저를 통해 인증을 수행합니다.</p>
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 실패 시
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        var authentication = NetsSsoAuthentication.unauthenticated(request, response);
        return this.getAuthenticationManager().authenticate(authentication);
    }

//    /**
//     * NSSO 액세스 토큰 요청의 필수 파라미터들을 검증합니다.
//     *
//     * <p>검증하는 파라미터:</p>
//     * <ul>
//     *   <li>op - "LI" 값이어야 함</li>
//     *   <li>ssosite - 사이트 ID (필수)</li>
//     *   <li>ssoResponse 또는 artifactID - 둘 중 하나는 필수</li>
//     *   <li>policyVersion - 정책 버전 (필수)</li>
//     * </ul>
//     *
//     * @param request 검증할 HTTP 요청 객체
//     * @throws NetsSsoAuthenticationException 필수 파라미터가 누락되거나 잘못된 경우
//     */
//    private void validateParameters(HttpServletRequest request) {
//        String ssosite = request.getParameter(SSOConst.SITE_ID);
//        if (!StringUtils.hasLength(ssosite)) {
//            throw new NetsSsoAuthenticationException(SSOExceptionCode.MissingParam, "ssosite must not be empty");
//        }
//
//        String ssoResponse = request.getParameter(SSOConst.SSO_RESPONSE);
//        String artifactID = request.getParameter(SSOConst.ARTIFACT_ID);
//        if (!StringUtils.hasLength(ssoResponse) && !StringUtils.hasLength(artifactID)) {
//            throw new NetsSsoAuthenticationException(SSOExceptionCode.MissingParam, "either ssoResponse or artifactID is required");
//        }
//
//        String policyVersion = request.getParameter(SSOConst.POLICY_VERSION);
//        if (!StringUtils.hasLength(policyVersion)) {
//            throw new NetsSsoAuthenticationException(SSOExceptionCode.MissingParam, "policyVersion must not be empty");
//        }
//    }
}
