package io.github.carped99.nsso.impl;

import io.github.carped99.nsso.NetsSsoUser;
import nets.sso.agent.web.v9.SSOUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * NetsSsoUserImpl 클래스의 단위 테스트
 *
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoUserImplTest {

    @Mock
    private SSOUser ssoUser;

    private Collection<GrantedAuthority> authorities;
    private Map<String, String> userAttributes;

    @BeforeEach
    void setUp() {
        authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        userAttributes = new HashMap<>();
        userAttributes.put("displayName", "Test User");
        userAttributes.put("email", "test@example.com");
        userAttributes.put("department", "IT");
    }

    @Test
    void constructor_ShouldCreateUserImpl() {
        // given
        when(ssoUser.getUserID()).thenReturn("testUser");
        when(ssoUser.getAttrs()).thenReturn(userAttributes);

        // when
        NetsSsoUserImpl userImpl = new NetsSsoUserImpl(ssoUser, authorities);

        // then
        assertThat(userImpl).isNotNull();
        assertThat(userImpl.getName()).isEqualTo("testUser");
        assertThat(userImpl.getAuthorities()).hasSize(2);
        assertThat(userImpl.getAttributes()).containsAllEntriesOf(userAttributes);
    }

    @Test
    void constructor_WithNullUser_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> new NetsSsoUserImpl(null, authorities))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("user must not be null");
    }

    @Test
    void constructor_WithNullAuthorities_ShouldUseEmptyAuthorities() {
        // given
        when(ssoUser.getAttrs()).thenReturn(userAttributes);

        // when
        NetsSsoUserImpl userImpl = new NetsSsoUserImpl(ssoUser, null);

        // then
        assertThat(userImpl).isNotNull();
        assertThat(userImpl.getAuthorities()).isEmpty();
    }

    @Test
    void getName_ShouldReturnUserId() {
        // given
        when(ssoUser.getUserID()).thenReturn("testUser123");
        when(ssoUser.getAttrs()).thenReturn(userAttributes);

        NetsSsoUserImpl userImpl = new NetsSsoUserImpl(ssoUser, authorities);

        // when
        String name = userImpl.getName();

        // then
        assertThat(name).isEqualTo("testUser123");
    }

    @Test
    void userImpl_ShouldBeInstanceOfNetsSsoUser() {
        // when
        NetsSsoUserImpl userImpl = new NetsSsoUserImpl(ssoUser, authorities);

        // then
        assertThat(userImpl).isInstanceOf(NetsSsoUser.class);
    }

    @Test
    void userImpl_ShouldBeInstanceOfAuthenticatedPrincipal() {
        // when
        NetsSsoUserImpl userImpl = new NetsSsoUserImpl(ssoUser, authorities);

        // then
        assertThat(userImpl).isInstanceOf(AuthenticatedPrincipal.class);
    }
} 