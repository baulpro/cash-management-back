package com.management.app.cash_management_back.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.management.app.cash_management_back.dto.request.AccountRequestDTO;
import com.management.app.cash_management_back.enums.AccountType;
import java.math.BigDecimal;
import java.util.UUID;
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
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO(
                "1234567890",
                AccountType.SAVING,
                BigDecimal.valueOf(500),
                true,
                UUID.randomUUID()
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnNotFoundWhenAccountNumberIsBlankAndClientDoesNotExist() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO(
                "",
                AccountType.SAVING,
                BigDecimal.valueOf(500),
                true,
                UUID.randomUUID()
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}