package io.github.carped99.nsso.mock;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

class ConverterUtils {
    private static final HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter();

    public static final String MOCK_COOKIE_NAME = "mocking-nsso-auth";

    private ConverterUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String writeAsString(Object value) {
        StringHttpOutputMessage outputMessage = new StringHttpOutputMessage();
        try {
            converter.write(value, null, outputMessage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return outputMessage.getBodyAsString(StandardCharsets.UTF_8);
    }

    public static String encodeUsername(String username) {
        return Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeUsername(String username) {
        return new String(Base64.getDecoder().decode(username));
    }

    public static String obtainUsername(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, MOCK_COOKIE_NAME))
                .map(Cookie::getValue)
                .map(ConverterUtils::decodeUsername)
                .orElse("");
    }
}
