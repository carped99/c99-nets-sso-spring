package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.v9.SSOAuthn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * NetsSsoAgentCheckServiceImpl 클래스의 단위 테스트
 *
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoAgentCheckServiceImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SSOAuthn ssoAuthn;

    private NetsSsoAgentCheckServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NetsSsoAgentCheckServiceImpl();
    }

    @Test
    void constructor_ShouldCreateService() {
        // then
        assertThat(service).isNotNull();
    }

    @Test
    void service_ShouldBeInstanceOfNetsSsoAgentCheckService() {
        // then
        assertThat(service).isInstanceOf(io.github.carped99.nsso.NetsSsoAgentCheckService.class);
    }

    @Test
    void process_ShouldReturnUserJson() {
        // given
        when(ssoAuthn.getUserJson()).thenReturn("{\"userId\":\"testUser\"}");

        try (MockedStatic<SSOAuthn> mockedSSOAuthn = mockStatic(SSOAuthn.class)) {
            mockedSSOAuthn.when(() -> SSOAuthn.get(any(), any())).thenReturn(ssoAuthn);

            // when
            String result = service.process(request, response);

            // then
            assertThat(result).isEqualTo("{\"userId\":\"testUser\"}");
            verify(ssoAuthn).authn();
        }
    }

    @Test
    void process_ShouldCallSSOAuthnMethods() {
        // given
        when(ssoAuthn.getUserJson()).thenReturn("{\"result\":\"success\"}");

        try (MockedStatic<SSOAuthn> mockedSSOAuthn = mockStatic(SSOAuthn.class)) {
            mockedSSOAuthn.when(() -> SSOAuthn.get(any(), any())).thenReturn(ssoAuthn);

            // when
            service.process(request, response);

            // then
            verify(ssoAuthn).authn();
            verify(ssoAuthn).getUserJson();
        }
    }

    @Test
    void process_WithSSOAuthnException_ShouldPropagateException() {
        // given
        RuntimeException expectedException = new RuntimeException("SSO Authn error");

        try (MockedStatic<SSOAuthn> mockedSSOAuthn = mockStatic(SSOAuthn.class)) {
            mockedSSOAuthn.when(() -> SSOAuthn.get(any(), any())).thenReturn(ssoAuthn);
            doThrow(expectedException).when(ssoAuthn).authn();

            // when & then
            assertThatThrownBy(() -> service.process(request, response))
                    .isEqualTo(expectedException);
        }
    }

    @Test
    void process_ShouldCreateWrappedRequest() {
        // given
        when(ssoAuthn.getUserJson()).thenReturn("{\"test\":\"data\"}");

        try (MockedStatic<SSOAuthn> mockedSSOAuthn = mockStatic(SSOAuthn.class)) {
            mockedSSOAuthn.when(() -> SSOAuthn.get(any(), any())).thenReturn(ssoAuthn);

            // when
            service.process(request, response);

            // then
            // SSOAuthn.get이 호출되었는지 확인 (wrapped request가 전달되었는지 간접적으로 확인)
            mockedSSOAuthn.verify(() -> SSOAuthn.get(any(), any()));
        }
    }
} 