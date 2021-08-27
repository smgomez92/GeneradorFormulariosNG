package com.fitbank.web.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filtro que setea X-UA-Compatible
 *
 * @author FitBank CI
 */
public class XUACompatibleFilter implements Filter {

    private String xua = "IE=8,chrome=1";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String ieParameter = filterConfig.getInitParameter("XUA");
        if (ieParameter != null) {
            this.xua = ieParameter;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse) response).addHeader("X-UA-Compatible", xua);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
