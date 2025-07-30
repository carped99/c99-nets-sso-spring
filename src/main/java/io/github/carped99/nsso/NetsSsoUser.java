package io.github.carped99.nsso;

import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * NSSO 사용자 정보를 나타내는 인터페이스
 * 
 * <p>이 인터페이스는 NSSO에서 인증된 사용자의 정보를 Spring Security와 통합하기 위한 어댑터 역할을 합니다.
 * Spring Security의 AuthenticatedPrincipal을 확장하여 사용자 속성과 권한 정보를 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 속성 관리</li>
 *   <li>권한 정보 제공</li>
 *   <li>타입 안전한 속성 접근</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * NetsSsoUser user = (NetsSsoUser) authentication.getPrincipal();
 * String userId = user.getName();
 * String email = user.getAttribute("email");
 * Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
 * }</pre>
 * 
 * @see org.springframework.security.core.AuthenticatedPrincipal
 * @see org.springframework.security.core.GrantedAuthority
 * 
 * @author carped99
 * @since 0.0.1
 */
public interface NetsSsoUser extends AuthenticatedPrincipal {
    /**
     * 지정된 이름의 속성을 원하는 타입으로 캐스팅하여 반환합니다.
     *
     * @param name 속성 이름
     * @param <A> 속성의 예상 타입
     * @return 속성 값, 존재하지 않는 경우 null
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <A> A getAttribute(String name) {
        return (A) getAttributes().get(name);
    }

    /**
     * 사용자의 모든 속성을 Map 형태로 반환합니다.
     * 
     * @return 사용자 속성 Map (변경 불가능한 Map이어야 함)
     */
    Map<String, Object> getAttributes();

    /**
     * 사용자의 권한 목록을 반환합니다.
     * 
     * @return 사용자 권한 목록
     */
    Collection<? extends GrantedAuthority> getAuthorities();
}
