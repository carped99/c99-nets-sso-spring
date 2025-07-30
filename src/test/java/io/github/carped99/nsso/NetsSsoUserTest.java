package io.github.carped99.nsso;

import nets.sso.agent.web.v9.SSOUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * NetsSsoUser 인터페이스의 기본 동작 테스트
 *
 * @author carped99
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoUserTest {

    @Test
    void netsSsoUser_ShouldExtendAuthenticatedPrincipal() {
        // given
        NetsSsoUser user = new NetsSsoUser(mock(SSOUser.class), null);

        // when & then
        assertThat(user).isInstanceOf(AuthenticatedPrincipal.class);
    }

    @Test
    void netsSsoUser_ShouldHaveBasicUserInfo() {
        // given
        SSOUser mock = mock(SSOUser.class);
        when(mock.getUserID()).thenReturn("testUser");

        NetsSsoUser user = new NetsSsoUser(mock, null);

        // when & then
        assertThat(user.getName()).isEqualTo("testUser");
        assertThat(user.getAuthorities()).isNotNull();
        assertThat(user.getAttributes()).isNotNull();
    }
}