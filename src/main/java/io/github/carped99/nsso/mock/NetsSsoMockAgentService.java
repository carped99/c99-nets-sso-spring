package io.github.carped99.nsso.mock;

import io.github.carped99.nsso.NetsSsoAgentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.github.carped99.nsso.NetsSsoUtils.normalizePath;

/**
 * NSSO Mock 에이전트 서비스
 *
 * <p>이 클래스는 NSSO Mock 서버에서 에이전트 관련 요청을 처리합니다.</p>
 *
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoMockAgentService implements NetsSsoAgentService {
    private final String prefixUrl;

    /**
     * 생성자
     *
     * @param prefixUrl NSSO Mock 서버의 URL 접두사
     */
    public NetsSsoMockAgentService(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    @Override
    public String check(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        return ConverterUtils.writeAsString(result);
    }

    @Override
    public String config(HttpServletRequest request, HttpServletResponse response) {
        // 1. Spring이 제공하는 HttpRequest로 래핑
        ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);

        // 2. URI 및 헤더 추출
        URI uri = serverHttpRequest.getURI();
        HttpHeaders headers = serverHttpRequest.getHeaders();

        // 3. Forwarded 헤더 기반으로 URI 재작성
        UriComponentsBuilder uriBuilder = ForwardedHeaderUtils.adaptFromForwardedHeaders(uri, headers);

        String contextPath = request.getContextPath();

        Map<String, Object> result = new HashMap<>();
        result.put("ssosite", "nets-sso-mock");
        result.put("urlSSOLogonService", normalizePath(contextPath, prefixUrl, NetsSsoMockServer.LOGON_PATH));
        result.put("urlSSOLogoffService", normalizePath(contextPath, prefixUrl, NetsSsoMockServer.LOGOFF_PATH));
        result.put("urlSSOCheckService", normalizePath(contextPath, prefixUrl, NetsSsoMockServer.CHECK_PATH));
        result.put("defaultUrl", uriBuilder.toUriString());

        return ConverterUtils.writeAsString(result);
    }

    @Override
    public String duplicate(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    @Override
    public String key(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    @Override
    public String tfa(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }
}
