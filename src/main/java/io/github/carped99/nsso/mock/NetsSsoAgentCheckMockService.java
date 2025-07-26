package io.github.carped99.nsso.mock;

import io.github.carped99.nsso.NetsSsoAgentCheckService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class NetsSsoAgentCheckMockService implements NetsSsoAgentCheckService {
    private final Customizer<Map<String, Object>> customizer;

    public NetsSsoAgentCheckMockService(@Nullable Customizer<Map<String, Object>> customizer) {
        this.customizer = Objects.requireNonNullElseGet(customizer, Customizer::withDefaults);
    }

    @SneakyThrows
    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();

        customizer.customize(result);

        return ConverterUtils.writeAsString(result);
    }
}
