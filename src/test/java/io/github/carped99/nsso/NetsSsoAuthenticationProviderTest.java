package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * NetsSsoAuthenticationProvider 클래스의 단위 테스트
 *
 * @author carped99
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoAuthenticationProviderTest {

    @Mock
    private NetsSsoAuthenticationService authenticationService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private GrantedAuthoritiesMapper authoritiesMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private NetsSsoAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        provider = new NetsSsoAuthenticationProvider(authenticationService, userDetailsService);
    }

    @Test
    void constructor_ShouldCreateProvider() {
        // then
        assertThat(provider).isNotNull();
    }

    @Test
    void constructor_WithNullAuthenticationService_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> new NetsSsoAuthenticationProvider(null, userDetailsService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("authenticationService must not be null");
    }

    @Test
    void constructor_WithNullUserDetailsService_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> new NetsSsoAuthenticationProvider(authenticationService, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userDetailsService must not be null");
    }

    @Test
    void supports_WithNetsSsoAuthentication_ShouldReturnTrue() {
        // when
        boolean result = provider.supports(NetsSsoAuthentication.class);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void supports_WithOtherAuthentication_ShouldReturnFalse() {
        // when
        boolean result = provider.supports(Authentication.class);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void authenticate_ShouldReturnAuthenticatedToken() {
        // given
        NetsSsoAuthentication unauthenticated = NetsSsoAuthentication.unauthenticated(request, response);
        NetsSsoAuthentication authenticated = NetsSsoAuthentication.authenticated("testUser", AuthorityUtils.NO_AUTHORITIES);
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(authenticationService.authenticate(any(NetsSsoAuthentication.class))).thenReturn(authenticated);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);

        // when
        Authentication result = provider.authenticate(unauthenticated);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    void authenticate_WithUserDetailsServiceException_ShouldThrowException() {
        // given
        NetsSsoAuthentication unauthenticated = NetsSsoAuthentication.unauthenticated(request, response);
        NetsSsoAuthentication authenticated = NetsSsoAuthentication.authenticated("testUser", AuthorityUtils.NO_AUTHORITIES);

        when(authenticationService.authenticate(any(NetsSsoAuthentication.class))).thenReturn(authenticated);
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("User not found"));

        // when & then
        assertThatThrownBy(() -> provider.authenticate(unauthenticated))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void setAuthoritiesMapper_ShouldSetAuthoritiesMapper() {
        // when
        provider.setAuthoritiesMapper(authoritiesMapper);

        // then
        // authoritiesMapper가 설정되었는지 확인하기 위해 authenticate 메서드 호출
        NetsSsoAuthentication unauthenticated = NetsSsoAuthentication.unauthenticated(request, response);
        NetsSsoAuthentication authenticated = NetsSsoAuthentication.authenticated("testUser", AuthorityUtils.NO_AUTHORITIES);
        UserDetails userDetails = User.withUsername("testUser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(authenticationService.authenticate(any(NetsSsoAuthentication.class))).thenReturn(authenticated);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(authoritiesMapper.mapAuthorities(any())).thenReturn(Collections.emptyList());

        Authentication result = provider.authenticate(unauthenticated);
        assertThat(result).isNotNull();
        verify(authoritiesMapper).mapAuthorities(userDetails.getAuthorities());
    }

    @Test
    void setAuthoritiesMapper_WithNull_ShouldThrowException() {
        // when & then
        assertThatThrownBy(() -> provider.setAuthoritiesMapper(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("authoritiesMapper must not be null");
    }

    @Test
    void provider_ShouldBeInstanceOfAuthenticationProvider() {
        // then
        assertThat(provider).isInstanceOf(AuthenticationProvider.class);
    }
}