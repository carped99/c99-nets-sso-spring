package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NetsSsoAgentKeyServiceImpl 클래스의 단위 테스트
 *
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoAgentKeyServiceImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private NetsSsoAgentKeyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NetsSsoAgentKeyServiceImpl();
    }

    @Test
    void constructor_ShouldCreateService() {
        // then
        assertThat(service).isNotNull();
    }

    @Test
    void service_ShouldBeInstanceOfNetsSsoAgentKeyService() {
        // then
        assertThat(service).isInstanceOf(io.github.carped99.nsso.NetsSsoAgentKeyService.class);
    }
} 