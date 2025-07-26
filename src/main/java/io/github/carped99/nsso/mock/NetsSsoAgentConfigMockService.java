package io.github.carped99.nsso.mock;

import io.github.carped99.nsso.NetsSsoAgentConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class NetsSsoAgentConfigMockService implements NetsSsoAgentConfigService {
    private final String prefixUrl;
    private final Customizer<Map<String, Object>> customizer;

    public NetsSsoAgentConfigMockService(String prefixUrl, @Nullable Customizer<Map<String, Object>> customizer) {
        this.prefixUrl = prefixUrl;
        this.customizer = Objects.requireNonNullElseGet(customizer, Customizer::withDefaults);
    }

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        // 1. Spring이 제공하는 HttpRequest로 래핑
        ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);

        // 2. URI 및 헤더 추출
        URI uri = serverHttpRequest.getURI();
        HttpHeaders headers = serverHttpRequest.getHeaders();

        // 3. Forwarded 헤더 기반으로 URI 재작성
        UriComponentsBuilder uriBuilder = ForwardedHeaderUtils.adaptFromForwardedHeaders(uri, headers);

        String contextPath = request.getContextPath();

        String basePath = contextPath + (prefixUrl.startsWith("/") ? prefixUrl : "/" + prefixUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("ssosite", "localhost");
        result.put("urlSSOLogonService", basePath + NetsSsoMockServer.LOGON_PATH);
        result.put("urlSSOLogoffService", basePath + NetsSsoMockServer.LOGOUT_PATH);
        result.put("urlSSOCheckService", basePath + NetsSsoMockServer.CHECK_PATH);
        result.put("defaultUrl", uriBuilder.toUriString());

        customizer.customize(result);

        return ConverterUtils.writeAsString(result);
    }
}
