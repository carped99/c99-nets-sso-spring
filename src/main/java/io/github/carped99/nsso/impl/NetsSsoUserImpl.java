package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoUser;
import nets.sso.agent.web.v9.SSOUser;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


/**
 * SSOUser를 Spring Security의 AuthenticatedPrincipal로 래핑하는 구현체
 * 
 * <p>이 클래스는 NSSO 에이전트의 SSOUser 객체를 Spring Security와 통합하기 위한 어댑터 역할을 합니다.
 * SSOUser의 정보를 NetsSsoUser 인터페이스에 맞게 변환하여 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>SSOUser 정보 래핑</li>
 *   <li>사용자 속성 관리</li>
 *   <li>권한 정보 제공</li>
 *   <li>불변 객체 보장</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * SSOUser ssoUser = authn.authn();
 * NetsSsoUser user = new NetsSsoUserImpl(ssoUser, authorities);
 * String userId = user.getName();
 * Map<String, Object> attributes = user.getAttributes();
 * }</pre>
 * 
 * @see SSOUser
 * @see NetsSsoUser
 * @see org.springframework.security.core.AuthenticatedPrincipal
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoUserImpl implements NetsSsoUser {
    private final SSOUser user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    /**
     * 주어진 SSO 사용자 정보로 NetsSsoUserImpl을 생성합니다.
     * 
     * <p>사용자의 속성이 null이거나 비어있는 경우, 불변의 빈 Map을 사용합니다.
     * 권한이 null인 경우 빈 권한 목록을 사용합니다.</p>
     *
     * @param user 래핑할 SSO 사용자 (null이 아니어야 함)
     * @param authorities 사용자 권한 목록 (null 가능)
     * @throws IllegalArgumentException user가 null인 경우
     */
    public NetsSsoUserImpl(SSOUser user, @Nullable Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(user, "user must not be null");
        this.user = user;
        this.authorities = Objects.requireNonNullElse(authorities, AuthorityUtils.NO_AUTHORITIES);

        var attrs = user.getAttrs();
        this.attributes = CollectionUtils.isEmpty(user.getAttrs()) ? Collections.emptyMap() : Collections.unmodifiableMap(attrs);
    }


    /**
     * 사용자 ID를 반환합니다.
     * 
     * @return 사용자 ID
     */
    @Override
    public String getName() {
        return user.getUserID();
    }

    /**
     * 사용자의 권한 목록을 반환합니다.
     * 
     * @return 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 사용자의 속성 Map을 반환합니다.
     * 
     * @return 불변 속성 Map
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
