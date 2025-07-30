package io.github.carped99.nsso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * NSSO 에이전트 요청을 처리하는 필터
 * 
 * <p>이 필터는 NSSO 에이전트의 다양한 요청 타입을 처리하는 중앙 집중식 필터입니다.
 * 요청 URL 패턴에 따라 적절한 서비스로 요청을 라우팅합니다.</p>
 * 
 * <p>지원하는 요청 타입:</p>
 * <ul>
 *   <li>체크 요청 (check) - 에이전트 상태 확인</li>
 *   <li>설정 요청 (config) - 에이전트 설정 관리</li>
 *   <li>중복 로그인 요청 (duplication) - 중복 로그인 처리</li>
 *   <li>2FA 요청 (tfa) - 2단계 인증 처리</li>
 *   <li>키 요청 (key) - 키 관리</li>
 * </ul>
 * 
 * <p>모든 응답은 JSON 형태로 반환되며, 오류 발생 시 표준 오류 형식으로 응답합니다.</p>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * NetsSsoAgentFilter filter = new NetsSsoAgentFilter();
 * filter.setCheckService(checkMatcher, checkService);
 * filter.setConfigService(configMatcher, configService);
 * // ... 다른 서비스들 설정
 * }</pre>
 * 
 * @see NetsSsoAgentCheckService
 * @see NetsSsoAgentConfigService
 * @see NetsSsoAgentDuplicateService
 * @see NetsSsoAgentTfaService
 * @see NetsSsoAgentKeyService
 * 
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAgentFilter extends OncePerRequestFilter {
    private RequestMatcher checkRequestMatcher;
    private RequestMatcher configRequestMatcher;
    private RequestMatcher dupRequestMatcher;
    private RequestMatcher tfaRequestMatcher;
    private RequestMatcher keyRequestMatcher;

    private NetsSsoAgentCheckService checkService;
    private NetsSsoAgentConfigService configService;
    private NetsSsoAgentDuplicateService dupService;
    private NetsSsoAgentTfaService tfaService;
    private NetsSsoAgentKeyService keyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String result;

        if (checkRequestMatcher != null && checkRequestMatcher.matches(request)) {
            result = tryProcess(() -> checkService.process(request, response));
        } else if (configRequestMatcher != null && configRequestMatcher.matches(request)) {
            result = tryProcess(() -> configService.process(request, response));
        } else if (dupRequestMatcher != null && dupRequestMatcher.matches(request)) {
            result = tryProcess(() -> dupService.process(request, response));
        } else if (tfaRequestMatcher != null && tfaRequestMatcher.matches(request)) {
            result = tryProcess(() -> tfaService.process(request, response));
        } else if (keyRequestMatcher != null && keyRequestMatcher.matches(request)) {
            result = tryProcess(() -> keyService.process(request, response));
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());

        // writer는 닫지 않는다.
        response.getWriter().write(result);
    }

    /**
     * 체크 서비스와 요청 매처를 설정합니다.
     * 
     * @param requestMatcher 체크 요청을 매칭하는 RequestMatcher
     * @param checkService 체크 요청을 처리하는 서비스
     * @throws IllegalArgumentException 매개변수가 null인 경우
     */
    public void setCheckService(RequestMatcher requestMatcher, NetsSsoAgentCheckService checkService) {
        Assert.notNull(requestMatcher, "requestMatcher must not be null");
        Assert.notNull(checkService, "checkService must not be null");
        this.checkRequestMatcher = requestMatcher;
        this.checkService = checkService;
    }

    /**
     * 설정 서비스와 요청 매처를 설정합니다.
     * 
     * @param requestMatcher 설정 요청을 매칭하는 RequestMatcher
     * @param configService 설정 요청을 처리하는 서비스
     * @throws IllegalArgumentException 매개변수가 null인 경우
     */
    public void setConfigService(RequestMatcher requestMatcher, NetsSsoAgentConfigService configService) {
        Assert.notNull(requestMatcher, "requestMatcher must not be null");
        Assert.notNull(configService, "configService must not be null");
        this.configRequestMatcher = requestMatcher;
        this.configService = configService;
    }

    /**
     * 중복 로그인 서비스와 요청 매처를 설정합니다.
     * 
     * @param dupRequestMatcher 중복 로그인 요청을 매칭하는 RequestMatcher
     * @param dupService 중복 로그인 요청을 처리하는 서비스
     * @throws IllegalArgumentException 매개변수가 null인 경우
     */
    public void setDupService(RequestMatcher dupRequestMatcher, NetsSsoAgentDuplicateService dupService) {
        Assert.notNull(dupRequestMatcher, "dupRequestMatcher must not be null");
        Assert.notNull(dupService, "dupService must not be null");
        this.dupRequestMatcher = dupRequestMatcher;
        this.dupService = dupService;
    }

    /**
     * 2FA 서비스와 요청 매처를 설정합니다.
     * 
     * @param tfaRequestMatcher 2FA 요청을 매칭하는 RequestMatcher
     * @param tfaService 2FA 요청을 처리하는 서비스
     * @throws IllegalArgumentException 매개변수가 null인 경우
     */
    public void setTfaService(RequestMatcher tfaRequestMatcher, NetsSsoAgentTfaService tfaService) {
        Assert.notNull(tfaRequestMatcher, "tfaRequestMatcher must not be null");
        Assert.notNull(tfaService, "tfaService must not be null");
        this.tfaRequestMatcher = tfaRequestMatcher;
        this.tfaService = tfaService;
    }

    /**
     * 키 서비스와 요청 매처를 설정합니다.
     * 
     * @param keyRequestMatcher 키 요청을 매칭하는 RequestMatcher
     * @param keyService 키 요청을 처리하는 서비스
     * @throws IllegalArgumentException 매개변수가 null인 경우
     */
    public void setKeyService(RequestMatcher keyRequestMatcher, NetsSsoAgentKeyService keyService) {
        Assert.notNull(keyRequestMatcher, "keyRequestMatcher must not be null");
        Assert.notNull(keyService, "keyService must not be null");
        this.keyRequestMatcher = keyRequestMatcher;
        this.keyService = keyService;
    }

    /**
     * 서비스 처리 중 발생하는 예외를 캐치하여 표준 오류 응답 형식으로 변환합니다.
     * 
     * @param process 실행할 서비스 처리 로직
     * @return 처리 결과 JSON 문자열 또는 오류 발생 시 표준 오류 JSON
     */
    private String tryProcess(Supplier<String> process) {
        try {
            return process.get();
        } catch (Exception ex) {
            // 오류코드는 8자리이며, "50"번대는 커스텀 오류
            return "{" +
                   "\"result\": false," +
                   "\"errorCode\": \"50000000\"," +
                   "\"errorMessage\": \"" + ex.getMessage() + "\"" +
                   "}";
        }
    }
}
