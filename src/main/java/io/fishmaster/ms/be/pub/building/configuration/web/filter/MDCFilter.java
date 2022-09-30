package io.fishmaster.ms.be.pub.building.configuration.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import io.fishmaster.ms.be.pub.building.utility.MDCUtility;

public class MDCFilter extends OncePerRequestFilter {

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var traceId = request.getHeader(MDCUtility.X_TRACE_ID_HEADER);

        MDCUtility.putTraceId(traceId);

        try {
            response.setHeader(MDCUtility.X_TRACE_ID_HEADER, MDCUtility.getTraceId());

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }

    }

}
