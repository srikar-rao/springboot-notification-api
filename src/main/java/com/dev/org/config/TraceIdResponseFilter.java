package com.dev.org.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to automatically inject the current Trace ID into all HTTP response headers.
 */
@Component
@RequiredArgsConstructor
public class TraceIdResponseFilter extends OncePerRequestFilter {

    private final ObjectProvider<Tracer> tracerProvider;

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    // Uncomment the next line if you also specifically want to support legacy B3
    // private static final String B3_TRACE_ID_HEADER = "X-B3-TraceId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Tracer tracer = tracerProvider.getIfAvailable();
        if (tracer != null) {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                String traceId = currentSpan.context().traceId();

                // Modern custom trace header
                response.setHeader(TRACE_ID_HEADER, traceId);

                // Legacy B3 header
                // response.setHeader(B3_TRACE_ID_HEADER, traceId);
            }
        }

        filterChain.doFilter(request, response);
    }
}
