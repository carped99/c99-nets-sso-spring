package io.github.carped99.nsso;

import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

/**
 * NSSO 유틸리티 클래스
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>경로 정규화</li>
 * </ul>
 *
 * @author carped99
 * @since 0.0.1
 */
public final class NetsSsoUtils {
    private NetsSsoUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 접두사와 접미사를 결합하여 정규화된 경로를 생성합니다.
     *
     * <p>UriComponentsBuilder를 사용하여 경로를 정규화하고 결합합니다.</p>
     *
     * @param segments 경로
     * @return 정규화된 경로
     * @throws IllegalArgumentException 경로가 null인 경우
     */
    public static String normalizePath(String... segments) {
        String[] tokens = StringUtils.tokenizeToStringArray(String.join("/", segments), "/");
        var path = UriComponentsBuilder.fromPath("/")
                .pathSegment(tokens)
                .build()
                .normalize()
                .getPath();
        return Objects.requireNonNull(path);
    }
}
