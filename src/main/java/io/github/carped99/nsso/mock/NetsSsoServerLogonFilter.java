package io.github.carped99.nsso.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nets.sso.agent.web.common.constant.SSOConst;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

class NetsSsoServerLogonFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserDetailsService userDetailsService;
    private final HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter();

    public NetsSsoServerLogonFilter(RequestMatcher requestMatcher, UserDetailsService userDetailsService) {
        Assert.notNull(requestMatcher, "requestMatcher may not be null");
        Assert.notNull(userDetailsService, "userDetailsService may not be null");
        this.requestMatcher = requestMatcher;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        NetsSsoServerLogonResponse result;

        try {
            String username = ServletRequestUtils.getRequiredStringParameter(request, SSOConst.USER_ID);
            String password = ServletRequestUtils.getRequiredStringParameter(request, SSOConst.USER_PW);

            // 인자 여부를 확인하기 위해
            ServletRequestUtils.getRequiredStringParameter(request, SSOConst.SITE_ID);
            ServletRequestUtils.getRequiredStringParameter(request, SSOConst.RETURN_URL);
            ServletRequestUtils.getRequiredStringParameter(request, SSOConst.CRED_TYPE);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (StringUtils.hasLength(userDetails.getPassword()) && !passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException(username);
            }
            result = getSuccessResponse(userDetails);
        } catch (Exception e) {
            result = getFailureResponse(e);
        }
        converter.write(result, null, new ServletServerHttpResponse(response));
    }

    private NetsSsoServerLogonResponse getSuccessResponse(UserDetails userDetails) {
        String username = userDetails.getUsername();

        // 사용자 정보를 ssoResponse로 전달
        String ssoResponse = ConverterUtils.encodeUsername(username);

        // 인증된 username 값을 ssoResponse 값으로 반환
        return NetsSsoServerLogonResponse.builder()
                .result(true)
                .errorCode(0)
                .errorMessage("")
                .policyVersion("456")
                .pathESSO("https://localhost:57291/")
                .ssoResponse(ssoResponse)
                .artifactESSO(UUID.randomUUID().toString())
                .build();
    }

    private NetsSsoServerLogonResponse getFailureResponse(Exception e) {
        int errorCode = 1; // 정의되지 않은 오류코드

        if (e instanceof UsernameNotFoundException) {
            errorCode = 11020003;
        } else if (e instanceof BadCredentialsException) {
            errorCode = 11020004;
        } else if (e instanceof MissingServletRequestParameterException ex) {
            String parameterName = ex.getParameterName();
            errorCode = switch (parameterName) {
                case SSOConst.SITE_ID -> 10000005; // 참여 사이트 도메인 정보가 전달되지 않았습니다
                case SSOConst.USER_ID -> 10000006; // 사용자 아이디 정보가 전달되지 않았습니다
                case SSOConst.USER_PW -> 10000007; // 사용자 비밀번호 정보가 전달되지 않았습니다
                case SSOConst.RETURN_URL -> 10000008; // ReturnUrl 정보가 전달되지 않았습니다
                case SSOConst.CRED_TYPE -> 10000009; // CredentialType 정보가 전달되지 않았습니다
                default -> 10000001; // 필수입력 값이 전달되지 않았습니다
            };
        }

        return NetsSsoServerLogonResponse.builder()
                .result(false)
                .errorCode(errorCode)
                .errorMessage(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage())
                .policyVersion("456")
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

    RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }
}
