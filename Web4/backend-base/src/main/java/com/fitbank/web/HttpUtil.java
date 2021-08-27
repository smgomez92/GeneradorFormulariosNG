package com.fitbank.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fitbank.util.IterableEnumeration;

public final class HttpUtil {

    private HttpUtil() {
    }

    /**
     * Formatea un HttpServletRequest util para imprimir y debug.
     * 
     * @param req
     *            el HttpServletRequest
     * @return String formateado
     */
    @SuppressWarnings("unchecked")
    public static String formatHttpServletRequest(HttpServletRequest req) {
        StringBuffer sb = new StringBuffer();

        sb.append(req.getMethod()
                + " "
                + req.getRequestURI()
                + (req.getQueryString() != null ? "?" + req.getQueryString()
                        : "") + "\n\n");

        for (String header : IterableEnumeration.get((Enumeration<String>) req
                .getHeaderNames())) {
            for (String value : IterableEnumeration
                    .get((Enumeration<String>) req.getHeaders(header))) {
                sb.append(header);
                sb.append('=');
                sb.append(value);
                sb.append('\n');
            }
        }

        sb.append('\n');

        for (String parameter : IterableEnumeration
                .get((Enumeration<String>) req.getParameterNames())) {
            for (String value : req.getParameterValues(parameter)) {
                sb.append(parameter);
                sb.append('=');
                sb.append(value);
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    /**
     * Formatea un HttpServletRequest util para imprimir y debug.
     * 
     * @param req
     *            el HttpServletRequest
     * @return String formateado
     */
    public static String formatHttpServletResponse(HttpServletResponse req) {
        StringBuffer sb = new StringBuffer(100);

        sb.append("Character-Encoding: ");
        sb.append(req.getCharacterEncoding());
        sb.append('\n');
        sb.append("Content-Type: ");
        sb.append(req.getContentType());

        return sb.toString();
    }
}
