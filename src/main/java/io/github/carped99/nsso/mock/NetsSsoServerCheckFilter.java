package io.github.carped99.nsso.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.v9.core.AuthnStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.github.carped99.nsso.mock.ConverterUtils.generateUserToken;
import static io.github.carped99.nsso.mock.ConverterUtils.obtainUsername;

class NetsSsoServerCheckFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher;
    private final HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter();

    public NetsSsoServerCheckFilter(RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher, "requestMatcher may not be null");
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        String username = obtainUsername(request);

        if (!StringUtils.hasText(username)) {
            result.put("result", false);
            result.put("authStatus", AuthnStatus.SSO_FIRST.name());
            result.put("errorCode", 50000000);
            result.put("errorMessage", "인증되지 않은 사용자입니다.");
        } else {
            result.put("result", true);
            result.put("authStatus", AuthnStatus.SSO_SUCCESS.name());
            result.put("userId", username);
            result.put("userAttribute", Map.of());
            result.put("token", generateUserToken(username));
        }

        converter.write(result, null, new ServletServerHttpResponse(response));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

    RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }
}
