package com.management.app.cash_management_back.dto.response;

import com.management.app.cash_management_back.enums.AccountType;
import java.math.BigDecimal;
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
public class AccountResponseDTO {
    private UUID accountId;
    private String accountNumber;
    private AccountType typeAccount;
    private BigDecimal originalBalance;
    private BigDecimal currentBalance;
    private Boolean status;
    private UUID clientId;
    private String clientName;
}