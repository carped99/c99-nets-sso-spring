package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * NetsSsoAuthentication 클래스의 단위 테스트
 * 
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoAuthenticationTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private Collection<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
    }

    @Test
    void authenticated_ShouldCreateAuthenticatedToken() {
        // given
        String principal = "testUser";

        // when
        NetsSsoAuthentication authentication = NetsSsoAuthentication.authenticated(principal, authorities);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(principal);
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities()).containsExactlyElementsOf(authorities);
        assertThat(authentication.getRequest()).isNull();
        assertThat(authentication.getResponse()).isNull();
    }

    @Test
    void unauthenticated_ShouldCreateUnauthenticatedToken() {
        // when
        NetsSsoAuthentication authentication = NetsSsoAuthentication.unauthenticated(request, response);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isFalse();
        assertThat(authentication.getPrincipal()).isNull();
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities()).isEmpty();
        assertThat(authentication.getRequest()).isEqualTo(request);
        assertThat(authentication.getResponse()).isEqualTo(response);
    }

    @Test
    void authenticated_WithNullPrincipal_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> NetsSsoAuthentication.authenticated(null, authorities))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("principal must not be null");
    }

    @Test
    void authenticated_WithNullAuthorities_ShouldWorkCorrectly() {
        // when
        NetsSsoAuthentication authentication = NetsSsoAuthentication.authenticated("user", null);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo("user");
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    void unauthenticated_WithNullRequest_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> NetsSsoAuthentication.unauthenticated(null, response))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("request must not be null");
    }

    @Test
    void unauthenticated_WithNullResponse_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> NetsSsoAuthentication.unauthenticated(request, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("response must not be null");
    }

    @Test
    void authenticated_WithUserObject_ShouldWorkCorrectly() {
        // given
        TestUser user = new TestUser("testUser", "Test User");

        // when
        NetsSsoAuthentication authentication = NetsSsoAuthentication.authenticated(user, authorities);

        // then
        assertThat(authentication.getPrincipal()).isEqualTo(user);
        assertThat(authentication.isAuthenticated()).isTrue();
    }

    @Test
    void setAuthenticated_ShouldChangeAuthenticationState() {
        // given
        NetsSsoAuthentication authentication = NetsSsoAuthentication.unauthenticated(request, response);

        // when
        authentication.setAuthenticated(true);

        // then
        assertThat(authentication.isAuthenticated()).isTrue();
    }

    /**
     * 테스트용 사용자 클래스
     */
    private static class TestUser {
        private final String id;
        private final String name;

        public TestUser(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
} 