package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoAuthentication;
import io.github.carped99.nsso.NetsSsoAuthenticationException;
import io.github.carped99.nsso.NetsSsoAuthenticationService;
import io.github.carped99.nsso.NetsSsoUser;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.common.constant.SSOConst;
import nets.sso.agent.web.common.exception.SSOException;
import nets.sso.agent.web.v9.SSOAuthn;
import nets.sso.agent.web.v9.SSOStatus;
import nets.sso.agent.web.v9.SSOUser;
import nets.sso.agent.web.v9.core.AuthnOperation;
import nets.sso.agent.web.v9.core.AuthnStatus;
import nets.sso.agent.web.v9.core.SSOConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * NSSO 인증 서비스의 기본 구현체
 *
 * <p>이 클래스는 NetsSsoAuthenticationService 인터페이스의 기본 구현체로,
 * NSSO 에이전트와의 실제 통신을 통해 사용자 인증을 수행합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>NSSO 에이전트와의 통신</li>
 *   <li>SSO 인증 처리</li>
 *   <li>사용자 정보 추출</li>
 *   <li>인증된 토큰 생성</li>
 * </ul>
 *
 * <p>처리 과정:</p>
 * <ol>
 *   <li>HTTP 요청을 NSSO 에이전트 타입으로 래핑</li>
 *   <li>SSO 인증 객체 초기화</li>
 *   <li>로그인 상태 확인</li>
 *   <li>사용자 정보 추출 및 토큰 생성</li>
 * </ol>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Service
 * public class CustomAuthenticationService extends NetsSsoAuthenticationServiceImpl {
 *     // 커스텀 로직 추가 가능
 * }
 * }</pre>
 *
 * @author carped99
 * @see NetsSsoAuthenticationService
 * @see SSOAuthn
 * @see SSOStatus
 * @since 0.0.1
 */
public class NetsSsoAuthenticationServiceImpl implements NetsSsoAuthenticationService {
    private final Log log = LogFactory.getLog(getClass());

    /**
     * NSSO 인증 토큰을 처리하여 인증된 Authentication 객체를 반환합니다.
     *
     * <p>인증 처리 과정:</p>
     * <ol>
     *   <li>HTTP 요청을 NSSO 에이전트 타입으로 래핑</li>
     *   <li>SSO 인증 객체 초기화</li>
     *   <li>로그인 상태 확인 (authnLoginStay)</li>
     *   <li>성공 시 사용자 정보 추출 및 인증된 토큰 생성</li>
     *   <li>실패 시 예외 발생</li>
     * </ol>
     *
     * @param authentication 처리할 NSSO 인증 토큰
     * @return 인증된 Authentication 객체
     * @throws NetsSsoAuthenticationException 인증 실패 시
     */
    @Override
    public Authentication authenticate(NetsSsoAuthentication authentication) {
        HttpServletRequest request = authentication.getRequest();
        HttpServletResponse response = authentication.getResponse();

        var wrappedRequest = new NetsSsoHttpServletRequestWrapper(request)
                .addSsoAgentType()
                .addHeader(SSOConst.OP, AuthnOperation.LOGIN.getValue());

        // 1) SSO 인증 객체 초기화
        SSOAuthn authn = SSOAuthn.get(wrappedRequest, response);
        SSOStatus status = authn.authnLoginStay();

        if (status.getStatus() == AuthnStatus.SSO_SUCCESS) {
            log.debug(String.format("SSO authenticated: code=%s, status=%s, message=%s", status.getCode(), status.getStatus(), status.getMessage()));
            SSOUser ssoUser = authn.authn();

            NetsSsoUser principal = new NetsSsoUserImpl(ssoUser, AuthorityUtils.NO_AUTHORITIES);
            NetsSsoAuthentication authenticated = NetsSsoAuthentication.authenticated(principal, principal.getAuthorities());
            authenticated.setDetails(new WebAuthenticationDetails(request));
            return authenticated;
        } else {
            throw ExceptionUtil.from(status);
        }
    }

    /**
     * NSSO 설정을 초기화합니다.
     *
     * <p>이 메서드는 빈 초기화 시점에 자동으로 호출되어 SSOConf를 초기화합니다.
     * 초기화 실패 시 IllegalStateException을 발생시킵니다.</p>
     *
     * @throws IllegalStateException SSOConf 초기화 실패 시
     */
    @PostConstruct
    void initialize() {
        try {
            SSOConf.getInstance();
        } catch (Exception e) {
            throw new IllegalStateException("SSOConf initialize error", e);
        }
    }
}
