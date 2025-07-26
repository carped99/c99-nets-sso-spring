package io.github.carped99.nsso;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NetsSsoUser 인터페이스의 기본 동작 테스트
 *
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoUserTest {

    @Test
    void netsSsoUser_ShouldExtendAuthenticatedPrincipal() {
        // given
        TestNetsSsoUser user = new TestNetsSsoUser("testUser", "Test User");

        // when & then
        assertThat(user).isInstanceOf(org.springframework.security.core.AuthenticatedPrincipal.class);
    }

    @Test
    void netsSsoUser_ShouldHaveBasicUserInfo() {
        // given
        TestNetsSsoUser user = new TestNetsSsoUser("testUser", "Test User");

        // when & then
        assertThat(user.getName()).isEqualTo("testUser");
        assertThat(user.getAuthorities()).isNotNull();
        assertThat(user.getAttributes()).isNotNull();
    }

    /**
     * 테스트용 NetsSsoUser 구현체
     */
    private static class TestNetsSsoUser implements NetsSsoUser {
        private final String name;
        private final String displayName;
        private final Collection<GrantedAuthority> authorities;

        public TestNetsSsoUser(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
            this.authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Collection<GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Map.of(
                "displayName", displayName,
                "userId", name,
                "email", name + "@example.com"
            );
        }
    }
} 