package io.github.carped99.nsso.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * NetsSsoHttpServletRequestWrapper 클래스의 단위 테스트
 *
 * @author tykim
 * @since 0.0.0
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoHttpServletRequestWrapperTest {

    @Mock
    private HttpServletRequest request;

    private NetsSsoHttpServletRequestWrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new NetsSsoHttpServletRequestWrapper(request);
    }

    @Test
    void constructor_ShouldCreateWrapper() {
        // then
        assertThat(wrapper).isNotNull();
    }

    @Test
    void addSsoAgentType_ShouldAddHeader() {
        // when
        wrapper.addSsoAgentType();

        // then
        assertThat(wrapper.getHeader("SSOAgent-Type")).isEqualTo("SPA");
    }

    @Test
    void addHeader_ShouldAddCustomHeader() {
        // given
        String headerName = "Custom-Header";
        String headerValue = "Custom-Value";

        // when
        wrapper.addHeader(headerName, headerValue);

        // then
        assertThat(wrapper.getHeader(headerName)).isEqualTo(headerValue);
    }

    @Test
    void getHeader_WithExistingHeader_ShouldReturnValue() {
        // given
        String headerName = "Test-Header";
        String headerValue = "Test-Value";
        wrapper.addHeader(headerName, headerValue);

        // when
        String result = wrapper.getHeader(headerName);

        // then
        assertThat(result).isEqualTo(headerValue);
    }

    @Test
    void getHeader_WithNonExistingHeader_ShouldReturnNull() {
        // when
        String result = wrapper.getHeader("Non-Existing-Header");

        // then
        assertThat(result).isNull();
    }

    @Test
    void getHeaders_WithExistingHeader_ShouldReturnEnumeration() {
        // given
        String headerName = "Test-Header";
        String headerValue = "Test-Value";
        wrapper.addHeader(headerName, headerValue);

        // when
        Enumeration<String> headers = wrapper.getHeaders(headerName);

        // then
        assertThat(headers).isNotNull();
        assertThat(Collections.list(headers)).containsExactly(headerValue);
    }

    @Test
    void getHeaders_WithNonExistingHeader_ShouldReturnEmptyEnumeration() {
        // when
        Enumeration<String> headers = wrapper.getHeaders("Non-Existing-Header");

        // then
        assertThat(headers).isNotNull();
        assertThat(Collections.list(headers)).isEmpty();
    }

    @Test
    void getHeaderNames_ShouldReturnAllHeaderNames() {
        // given
        wrapper.addHeader("Header1", "Value1");
        wrapper.addHeader("Header2", "Value2");

        // when
        Enumeration<String> headerNames = wrapper.getHeaderNames();

        // then
        assertThat(headerNames).isNotNull();
        assertThat(Collections.list(headerNames)).contains("Header1", "Header2");
    }

    @Test
    void addMultipleHeaders_ShouldWorkCorrectly() {
        // given
        wrapper.addHeader("Header1", "Value1");
        wrapper.addHeader("Header2", "Value2");
        wrapper.addSsoAgentType();

        // when & then
        assertThat(wrapper.getHeader("Header1")).isEqualTo("Value1");
        assertThat(wrapper.getHeader("Header2")).isEqualTo("Value2");
        assertThat(wrapper.getHeader("SSOAgent-Type")).isEqualTo("SPA");
    }
} 