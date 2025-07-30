package io.github.carped99.nsso.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class NetsSsoServerCheckFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher;

    private final HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter();

    public NetsSsoServerCheckFilter(RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher, "requestMatcher may not be null");
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        NetsSsoServerCheckResponse result;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            result = failure();
        } else {
            result = success(authentication);
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

    private NetsSsoServerCheckResponse success(Authentication authentication) {
        String username = authentication.getName();
        String token = Base64.getEncoder().withoutPadding().encodeToString(username.getBytes(StandardCharsets.UTF_8));

        return NetsSsoServerCheckResponse.builder()
                .result(true)
                .userId(username)
                .authStatus("SSO_SUCCESS")
                .token(token)
                .build();
    }

    private NetsSsoServerCheckResponse failure() {
        return NetsSsoServerCheckResponse.builder()
                .result(false)
                .build();
    }
}
