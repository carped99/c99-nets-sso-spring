package io.github.carped99.nsso;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * NSSO 인증 제공자
 * 
 * <p>이 클래스는 Spring Security의 AuthenticationProvider를 구현하여
 * NSSO 인증 토큰을 처리하고 UserDetailsService를 통해 사용자 정보를 로드합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>NSSO 인증 토큰 처리</li>
 *   <li>UserDetailsService를 통한 사용자 정보 로드</li>
 *   <li>권한 매핑 및 변환</li>
 *   <li>인증된 토큰 생성</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Bean
 * public AuthenticationProvider netsSsoAuthenticationProvider(
 *         NetsSsoAuthenticationService authenticationService,
 *         UserDetailsService userDetailsService) {
 *     return new NetsSsoAuthenticationProvider(authenticationService, userDetailsService);
 * }
 * }</pre>
 * 
 * @see org.springframework.security.authentication.AuthenticationProvider
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see NetsSsoAuthentication
 * @see NetsSsoAuthenticationService
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final NetsSsoAuthenticationService authenticationService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /**
     * NetsSsoAuthenticationProvider를 생성합니다.
     * 
     * @param authenticationService NSSO 인증 서비스
     * @param userDetailsService 사용자 정보 서비스
     * @throws IllegalArgumentException 매개변수가 null인 경우
     */
    public NetsSsoAuthenticationProvider(NetsSsoAuthenticationService authenticationService, UserDetailsService userDetailsService) {
        Assert.notNull(authenticationService, "authenticationService must not be null");
        Assert.notNull(userDetailsService, "userDetailsService must not be null");
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * NSSO 인증 토큰을 처리하여 인증된 Authentication 객체를 반환합니다.
     * 
     * <p>처리 과정:</p>
     * <ol>
     *   <li>NSSO 인증 서비스를 통해 인증 수행</li>
     *   <li>UserDetailsService를 통해 사용자 정보 로드</li>
     *   <li>권한 매핑을 통해 최종 권한 생성</li>
     *   <li>인증된 토큰 생성 및 반환</li>
     * </ol>
     * 
     * @param authentication 처리할 인증 토큰
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 실패 시
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var authenticated = authenticationService.authenticate((NetsSsoAuthentication) authentication);
        var userDetails = userDetailsService.loadUserByUsername(authenticated.getName());

        var result = NetsSsoAuthentication.authenticated(userDetails, this.authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * 이 제공자가 NetsSsoAuthentication 타입을 지원하는지 확인합니다.
     * 
     * @param authentication 확인할 인증 클래스
     * @return NetsSsoAuthentication 또는 그 하위 클래스인 경우 true
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return NetsSsoAuthentication.class.isAssignableFrom(authentication);
    }

    /**
     * 권한 매핑기를 설정합니다.
     * 
     * @param authoritiesMapper 권한 매핑기
     * @throws IllegalArgumentException authoritiesMapper가 null인 경우
     */
    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        Assert.notNull(authoritiesMapper, "authoritiesMapper must not be null");
        this.authoritiesMapper = authoritiesMapper;
    }
}
