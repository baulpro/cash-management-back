package com.management.app.cash_management_back.dto.request;

import com.management.app.cash_management_back.dto.groups.CreateValidation;
import com.management.app.cash_management_back.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

  @NotBlank(message = "Account number is required")
  private String accountNumber;
  @NotNull(message = "Account type is required")
  private AccountType typeAccount;
  @NotNull(groups = CreateValidation.class, message = "Original balance is required")
  @PositiveOrZero(groups = CreateValidation.class)
  private BigDecimal originalBalance;
  @NotNull
  private Boolean status;
  @NotNull(message = "Client ID is required")
  private UUID clientId;

}
