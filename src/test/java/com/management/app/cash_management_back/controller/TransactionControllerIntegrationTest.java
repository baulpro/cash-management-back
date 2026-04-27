package com.management.app.cash_management_back.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.management.app.cash_management_back.dto.request.TransactionRequestDTO;
import com.management.app.cash_management_back.enums.TransactionType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO(
                "9999999999",
                TransactionType.DEBIT,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnBadRequestWhenAmountIsNegative() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO(
                "1234567890",
                TransactionType.DEBIT,
                BigDecimal.valueOf(-100)
        );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}