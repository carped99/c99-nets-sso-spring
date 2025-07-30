package io.github.carped99.nsso.mock;

import io.github.carped99.nsso.NetsSsoAuthentication;
import io.github.carped99.nsso.NetsSsoAuthenticationService;
import io.github.carped99.nsso.NetsSsoUser;
import io.github.carped99.nsso.impl.NetsSsoUserImpl;
import jakarta.servlet.http.HttpServletRequest;
import nets.sso.agent.web.common.constant.SSOConst;
import nets.sso.agent.web.v9.SSOUser;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;

/**
 * NSSO 인증 서비스의 Mock 구현체
 * 
 * <p>이 클래스는 개발 및 테스트 환경에서 실제 NSSO 서버 없이도 인증을 시뮬레이션하는 Mock 서비스입니다.
 * 실제 NSSO 인증 플로우를 모방하여 테스트용 사용자 인증을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>테스트용 사용자 인증 시뮬레이션</li>
 *   <li>사용자명 추출 (username 파라미터 또는 ssoResponse 디코딩)</li>
 *   <li>Mock SSOUser 생성</li>
 *   <li>인증된 토큰 생성</li>
 * </ul>
 * 
 * <p>사용자명 추출 우선순위:</p>
 * <ol>
 *   <li>username 파라미터 (테스트용)</li>
 *   <li>ssoResponse 파라미터 (Base64 디코딩)</li>
 *   <li>기본값 "user"</li>
 * </ol>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Profile("nsso-mock")
 * @Service
 * public class MockAuthenticationService extends NetsSsoAuthenticationMockService {
 *     // 커스텀 Mock 로직 추가 가능
 * }
 * }</pre>
 * 
 * @see io.github.carped99.nsso.NetsSsoAuthenticationService
 * @see nets.sso.agent.web.v9.SSOUser
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAuthenticationMockService implements NetsSsoAuthenticationService {
    /** 인증 상세 정보 소스 */
    protected AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    /**
     * Mock NSSO 인증을 수행합니다.
     * 
     * <p>요청에서 사용자명을 추출하고 Mock SSOUser를 생성하여
     * 인증된 Authentication 객체를 반환합니다.</p>
     * 
     * @param authentication 처리할 NSSO 인증 토큰
     * @return 인증된 Authentication 객체
     */
    @Override
    public Authentication authenticate(NetsSsoAuthentication authentication) {
        HttpServletRequest request = authentication.getRequest();

        // ssoResponse에 사용자 이름
        String username = obtainUsername(request);

        if (request.getParameter(SSOConst.OP) == null) {
            username = "user";
        }

        // 사용자 검증
        SSOUser ssoUser = findUser(username, request);

        NetsSsoUser principal = new NetsSsoUserImpl(ssoUser, AuthorityUtils.NO_AUTHORITIES);
        var authenticated = NetsSsoAuthentication.authenticated(principal, principal.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetails(request));
        return authenticated;
    }

    /**
     * 요청에서 사용자명을 추출합니다.
     * 
     * <p>다음 순서로 사용자명을 추출합니다:</p>
     * <ol>
     *   <li>username 파라미터 (테스트용)</li>
     *   <li>ssoResponse 파라미터 (Base64 디코딩)</li>
     *   <li>빈 문자열</li>
     * </ol>
     * 
     * @param request HTTP 요청 객체
     * @return 추출된 사용자명
     */
    private String obtainUsername(HttpServletRequest request) {
        // 테스트 용도로 사용자의 이름을 받는다.
        String username = request.getParameter("username");
        if (StringUtils.hasText(username)) {
            return username;
        }

        String ssoResponse = request.getParameter(SSOConst.SSO_RESPONSE);
        if (StringUtils.hasText(ssoResponse)) {
            return new String(Base64.getDecoder().decode(ssoResponse));
        }
        return "";
    }

    /**
     * 사용자 검증 및 Mock SSOUser 생성
     * 
     * <p>실제 검증 없이 항상 유효한 Mock SSOUser를 생성합니다.
     * 요청 정보를 기반으로 사용자 정보를 구성합니다.</p>
     * 
     * @param username 사용자명
     * @param request HTTP 요청 객체
     * @return Mock SSOUser 객체
     */
    private SSOUser findUser(String username, HttpServletRequest request) {
        // 항상 사용자를 반환
        WebAuthenticationDetails details = authenticationDetailsSource.buildDetails(request);
        return new SSOUser(username, details.getRemoteAddress(), details.getSessionId(), new Date(), new Date());
    }
}