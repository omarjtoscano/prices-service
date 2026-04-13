package com.bcnc.pricing.infrastructure.adapter.in.rest;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;

class MdcRequestFilterTest {

    private final MdcRequestFilter filter = new MdcRequestFilter();

    @Test
    @DisplayName("Should generate UUID and set X-Request-ID response header when no header is provided")
    void shouldGenerateRequestId_whenNoHeaderProvided() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        String responseHeader = response.getHeader("X-Request-ID");
        assertThat(responseHeader).isNotNull();
        assertThat(UUID.fromString(responseHeader)).isNotNull();
    }

    @Test
    @DisplayName("Should reuse incoming X-Request-ID header value")
    void shouldReuseRequestId_whenHeaderProvided() throws ServletException, IOException {
        String existingId = "custom-request-id-123";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Request-ID", existingId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Request-ID")).isEqualTo(existingId);
    }

    @Test
    @DisplayName("Should generate new UUID when X-Request-ID header is blank")
    void shouldGenerateRequestId_whenHeaderIsBlank() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Request-ID", "   ");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        String responseHeader = response.getHeader("X-Request-ID");
        assertThat(responseHeader).isNotNull();
        assertThat(responseHeader).isNotBlank();
    }

    @Test
    @DisplayName("Should invoke the filter chain")
    void shouldInvokeFilterChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
        assertThat(filterChain.getResponse()).isNotNull();
    }
}
