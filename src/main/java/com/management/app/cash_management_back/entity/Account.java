package com.management.app.cash_management_back.entity;

import com.management.app.cash_management_back.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "account")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "account_id")
  private UUID accountId;

  @Column(name = "account_number", unique = true, nullable = false)
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "type_account",  nullable = false)
  private AccountType typeAccount;

  @Column(name = "original_balance", nullable = false)
  private BigDecimal originalBalance;

  @Column(name = "current_balance", nullable = false)
  private BigDecimal currentBalance;

  @Column(nullable = false)
  private Boolean status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

}
