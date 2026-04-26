package com.management.app.cash_management_back.dto.response;

import com.management.app.cash_management_back.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private UUID transactionId;
    private String accountNumber;
    private LocalDateTime date;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private String clientName;
}