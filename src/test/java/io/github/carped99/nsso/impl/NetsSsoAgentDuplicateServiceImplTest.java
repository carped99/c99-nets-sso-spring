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
 * NetsSsoAgentDuplicateServiceImpl 클래스의 단위 테스트
 *
 * @author carped99
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoAgentDuplicateServiceImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private NetsSsoAgentDuplicateServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NetsSsoAgentDuplicateServiceImpl();
    }

    @Test
    void constructor_ShouldCreateService() {
        // then
        assertThat(service).isNotNull();
    }

    @Test
    void service_ShouldBeInstanceOfNetsSsoAgentDuplicateService() {
        // then
        assertThat(service).isInstanceOf(io.github.carped99.nsso.NetsSsoAgentDuplicateService.class);
    }
} 