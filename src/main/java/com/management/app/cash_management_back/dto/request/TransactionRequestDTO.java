package com.management.app.cash_management_back.dto.request;

import com.management.app.cash_management_back.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {

  @NotBlank(message = "Account number is required")
  private String accountNumber;
  @NotNull(message = "Transaction type is required")
  private TransactionType transactionType;
  @NotNull
  @Positive(message = "Amount must be positive")
  private BigDecimal amount;

}
