package io.github.carped99.nsso.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

class NetsSsoServerLogoutFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher;
    private final HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter();

    public NetsSsoServerLogoutFilter(RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher, "requestMatcher may not be null");
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        NetsSsoServerLogoutResponse result = NetsSsoServerLogoutResponse.builder().build();
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
