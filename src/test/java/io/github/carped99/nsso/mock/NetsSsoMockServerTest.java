package io.github.carped99.nsso.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * NetsSsoMockServer 클래스의 단위 테스트
 *
 * @author carped99
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoMockServerTest {

    @Mock
    private HttpSecurityBuilder<?> httpSecurityBuilder;

    @Mock
    private UserDetailsService userDetailsService;

    private NetsSsoMockServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = new NetsSsoMockServer();
        mockServer.setUserDetailsService(userDetailsService);
    }

    @Test
    void constructor_ShouldCreateMockServer() {
        // then
        assertThat(mockServer).isNotNull();
    }

    @Test
    void setPrefixUrl_ShouldSetPrefixPath() {
        // given
        String prefixUrl = "/test/nsso";

        // when
        NetsSsoMockServer result = mockServer.setPrefixPath(prefixUrl);

        // then
        assertThat(result).isEqualTo(mockServer);
    }

    @Test
    void setUserDetailsService_ShouldSetUserDetailsService() {
        // when
        NetsSsoMockServer result = mockServer.setUserDetailsService(userDetailsService);

        // then
        assertThat(result).isEqualTo(mockServer);
    }

    @Test
    void configure_ShouldAddFiltersToHttpSecurity() {
        // given
        String prefixUrl = "/nsso";
        mockServer.setPrefixPath(prefixUrl);
        mockServer.setUserDetailsService(userDetailsService);

        // when
        mockServer.configure(httpSecurityBuilder);

        // then
        // HttpSecurityBuilder에 필터들이 추가되었는지 확인
        // 실제로는 verify를 통해 확인할 수 있지만, 여기서는 기본적인 동작 확인만 수행
        verify(httpSecurityBuilder, times(3)).addFilterAfter(any(), any());
    }

    @Test
    void getRequestMatcher_ShouldReturnRequestMatcher() {
        // given
        String prefixUrl = "/nsso";
        mockServer.setPrefixPath(prefixUrl);
        mockServer.configure(httpSecurityBuilder);

        // when
        RequestMatcher result = mockServer.getRequestMatcher();

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void mockServer_ShouldBeConfigurable() {
        // given
        String prefixUrl = "/custom/nsso";

        // when
        NetsSsoMockServer configuredServer = mockServer
                .setPrefixPath(prefixUrl)
                .setUserDetailsService(userDetailsService);

        // then
        assertThat(configuredServer).isEqualTo(mockServer);
    }
} 