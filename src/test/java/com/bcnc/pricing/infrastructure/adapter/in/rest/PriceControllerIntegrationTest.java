package com.bcnc.pricing.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/api/v1/prices";
    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;

    @Test
    @DisplayName("Test 1: 14/06 10:00 → priceList=1, price=35.50")
    void shouldReturnPriceList1_whenRequestAt10OnDay14() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    @Test
    @DisplayName("Test 2: 14/06 16:00 → priceList=2, price=25.45")
    void shouldReturnPriceList2_whenRequestAt16OnDay14() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45));
    }

    @Test
    @DisplayName("Test 3: 14/06 21:00 → priceList=1, price=35.50")
    void shouldReturnPriceList1_whenRequestAt21OnDay14() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T21:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    @Test
    @DisplayName("Test 4: 15/06 10:00 → priceList=3, price=30.50")
    void shouldReturnPriceList3_whenRequestAt10OnDay15() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-15T10:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50));
    }

    @Test
    @DisplayName("Test 5: 16/06 21:00 → priceList=4, price=38.95")
    void shouldReturnPriceList4_whenRequestAt21OnDay16() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-16T21:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95));
    }

    // --- Negative scenarios ---

    @Test
    @DisplayName("Error 404: unknown product → No applicable price found")
    void shouldReturn404_whenNoApplicablePriceExists() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "99999")
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("No applicable price found")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: missing applicationDate → Missing required parameter")
    void shouldReturn400_whenApplicationDateIsMissing() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Missing required parameter: 'applicationDate'"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: missing productId → Missing required parameter")
    void shouldReturn400_whenProductIdIsMissing() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Missing required parameter: 'productId'"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: productId=abc → Invalid value for parameter")
    void shouldReturn400_whenProductIdIsNotANumber() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "abc")
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Invalid value for parameter 'productId'")))
                .andExpect(jsonPath("$.message").value(containsString("abc")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: applicationDate=2020-06-14 (no time) → Invalid value for parameter")
    void shouldReturn400_whenApplicationDateHasInvalidFormat() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Invalid value for parameter 'applicationDate'")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: productId=0 → must be positive")
    void shouldReturn400_whenProductIdIsZero() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "0")
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("productId")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: productId=-1 → must be positive")
    void shouldReturn400_whenProductIdIsNegative() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "-1")
                        .param("brandId", BRAND_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("productId")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: brandId=0 → must be positive")
    void shouldReturn400_whenBrandIdIsZero() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("brandId")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Error 400: brandId=-1 → must be positive")
    void shouldReturn400_whenBrandIdIsNegative() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("brandId")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
