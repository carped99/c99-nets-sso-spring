package io.github.carped99.nsso.configure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * NetsSsoConfigurerUtils 클래스의 단위 테스트
 *
 * @author carped99
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class NetsSsoConfigurerUtilsTest {

    @Test
    void normalizePath_WithEmpty() {
        // when
        String result = NetsSsoConfigurerUtils.normalizePath();

        // then
        assertThat(result).isEqualTo("/");
    }

    @Test
    void normalizePath_WithValidPrefixAndSuffix_ShouldReturnCombinedPath() {
        // given
        String prefix = "/nsso";
        String suffix = "/check";

        // when
        String result = NetsSsoConfigurerUtils.normalizePath(prefix, suffix);

        // then
        assertThat(result).isEqualTo("/nsso/check");
    }

    @Test
    void normalizePath_WithTrailingSlash_ShouldNormalizePath() {
        // given
        String prefix = "/nsso/";
        String suffix = "/check/";

        // when
        String result = NetsSsoConfigurerUtils.normalizePath(prefix, suffix);

        // then
        assertThat(result).isEqualTo("/nsso/check");
    }

    @Test
    void normalizePath_WithEmptySuffix_ShouldReturnPrefix() {
        // given
        String prefix = "/nsso";
        String suffix = "";

        // when
        String result = NetsSsoConfigurerUtils.normalizePath(prefix, suffix);

        // then
        assertThat(result).isEqualTo("/nsso");
    }

    @Test
    void normalizePath_WithComplexPath_ShouldNormalizeCorrectly() {
        // given
        String prefix = "/api/v1/nsso";
        String suffix = "/agent/check";

        // when
        String result = NetsSsoConfigurerUtils.normalizePath(prefix, suffix);

        // then
        assertThat(result).isEqualTo("/api/v1/nsso/agent/check");
    }

    @Test
    void normalizePath_WithRootPrefix_ShouldWorkCorrectly() {
        // given
        String prefix = "/";
        String suffix = "check";

        // when
        String result = NetsSsoConfigurerUtils.normalizePath(prefix, suffix);

        // then
        assertThat(result).isEqualTo("/check");
    }

    @Test
    void normalizePath_WithMultipleSlashes_ShouldNormalizeCorrectly() {
        // given
        String prefix = "/nsso///";
        String suffix = "///check";

        // when
        String result = NetsSsoConfigurerUtils.normalizePath(prefix, suffix);

        // then
        assertThat(result).isEqualTo("/nsso/check");
    }
} 