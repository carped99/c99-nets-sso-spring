package io.github.carped99.nsso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static io.github.carped99.nsso.NetsSsoUtils.normalizePath;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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
 * @author carped99
 * @since 0.0.1
 */
public class NetsSsoAgentFilter extends OncePerRequestFilter {
    private final NetsSsoAgentService agentService;
    private final RequestMatcher checkRequestMatcher;
    private final RequestMatcher configRequestMatcher;
    private final RequestMatcher dupRequestMatcher;
    private final RequestMatcher tfaRequestMatcher;
    private final RequestMatcher keyRequestMatcher;

    /**
     * 생성자
     *
     * @param prefixPath NSSO 에이전트 요청의 접두사 경로
     * @param agentService NSSO 에이전트 서비스 인스턴스
     */
    public NetsSsoAgentFilter(String prefixPath, NetsSsoAgentService agentService) {
        Assert.notNull(agentService, "agentService must not be null");
        this.agentService = agentService;
        this.checkRequestMatcher = antMatcher(HttpMethod.POST, normalizePath(prefixPath, "/check"));
        this.configRequestMatcher = antMatcher(HttpMethod.POST, normalizePath(prefixPath, "/config"));
        this.dupRequestMatcher = antMatcher(HttpMethod.POST, normalizePath(prefixPath, "/duplication"));
        this.tfaRequestMatcher = antMatcher(HttpMethod.POST, normalizePath(prefixPath, "/tfa"));
        this.keyRequestMatcher = antMatcher(HttpMethod.POST, normalizePath(prefixPath, "/key"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String result;

        if (checkRequestMatcher != null && checkRequestMatcher.matches(request)) {
            result = tryProcess(() -> agentService.check(request, response));
        } else if (configRequestMatcher != null && configRequestMatcher.matches(request)) {
            result = tryProcess(() -> agentService.config(request, response));
        } else if (dupRequestMatcher != null && dupRequestMatcher.matches(request)) {
            result = tryProcess(() -> agentService.duplicate(request, response));
        } else if (tfaRequestMatcher != null && tfaRequestMatcher.matches(request)) {
            result = tryProcess(() -> agentService.tfa(request, response));
        } else if (keyRequestMatcher != null && keyRequestMatcher.matches(request)) {
            result = tryProcess(() -> agentService.key(request, response));
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
     * NSSO 에이전트 요청을 처리할 RequestMatcher를 반환합니다.
     *
     * <p>이 메서드는 NSSO 에이전트의 다양한 요청 타입에 대한 RequestMatcher를
     * OrRequestMatcher로 결합하여 반환합니다.</p>
     *
     * @return NSSO 에이전트 요청을 처리할 RequestMatcher
     */
    public RequestMatcher getRequestMatcher() {
        return new OrRequestMatcher(
                checkRequestMatcher,
                configRequestMatcher,
                dupRequestMatcher,
                tfaRequestMatcher,
                keyRequestMatcher
        );
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
