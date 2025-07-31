package io.github.carped99.nsso.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

final class NetsSsoNoBodyHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private final Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>();

    public NetsSsoNoBodyHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(OutputStream.nullOutputStream());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new DummyOutputStream();
    }

    private static class DummyOutputStream extends ServletOutputStream {
        @Override
        public void write(int b) {
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }
}
