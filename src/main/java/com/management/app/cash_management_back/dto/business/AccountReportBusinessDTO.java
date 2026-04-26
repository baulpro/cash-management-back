package com.management.app.cash_management_back.dto.business;

import com.management.app.cash_management_back.enums.AccountType;
import java.math.BigDecimal;
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
public class AccountReportBusinessDTO {

  private String accountNumber;
  private AccountType typeAccount;
  private BigDecimal originalBalance;
  private BigDecimal currentBalance;
  private Long creditCount;
  private Long debitCount;
  private BigDecimal totalCredits;
  private BigDecimal totalDebits;
}
