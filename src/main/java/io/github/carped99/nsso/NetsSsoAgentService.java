package io.github.carped99.nsso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface NetsSsoAgentService {
    String check(HttpServletRequest request, HttpServletResponse response);
    String config(HttpServletRequest request, HttpServletResponse response);
    String duplicate(HttpServletRequest request, HttpServletResponse response);
    String key(HttpServletRequest request, HttpServletResponse response);
    String tfa(HttpServletRequest request, HttpServletResponse response);
}
