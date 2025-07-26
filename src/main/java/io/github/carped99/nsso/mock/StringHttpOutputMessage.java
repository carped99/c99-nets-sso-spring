package io.github.carped99.nsso.mock;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class StringHttpOutputMessage implements HttpOutputMessage {
    private final HttpHeaders headers = new HttpHeaders();
    private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public OutputStream getBody() throws IOException {
        return this.body;
    }

    public byte[] getBodyAsBytes() {
        return this.body.toByteArray();
    }

    public String getBodyAsString() {
        return this.getBodyAsString(StandardCharsets.UTF_8);
    }

    public String getBodyAsString(Charset charset) {
        return StreamUtils.copyToString(this.body, charset);
    }


}

