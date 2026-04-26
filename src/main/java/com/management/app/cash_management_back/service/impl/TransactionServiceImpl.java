package com.management.app.cash_management_back.service.impl;

import com.management.app.cash_management_back.constants.BusinessConstants;
import com.management.app.cash_management_back.dto.request.TransactionRequestDTO;
import com.management.app.cash_management_back.dto.response.TransactionResponseDTO;
import com.management.app.cash_management_back.entity.Account;
import com.management.app.cash_management_back.entity.Transaction;
import com.management.app.cash_management_back.enums.TransactionType;
import com.management.app.cash_management_back.exception.BusinessException;
import com.management.app.cash_management_back.exception.ResourceNotFoundException;
import com.management.app.cash_management_back.mapper.TransactionMapper;
import com.management.app.cash_management_back.repository.AccountRepository;
import com.management.app.cash_management_back.repository.TransactionRepository;
import com.management.app.cash_management_back.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final TransactionMapper transactionMapper;

  @Override
  public TransactionResponseDTO create(TransactionRequestDTO dto) {
    Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
        .orElseThrow(
            () -> new ResourceNotFoundException("Account not found: " + dto.getAccountNumber()));

    if (Boolean.FALSE.equals(account.getStatus())) {
      throw new BusinessException("Cannot make transactions on an inactive account");
    }

    if (Boolean.FALSE.equals(account.getClient().getStatus())) {
      throw new BusinessException("Cannot make transactions on accounts of inactive clients");
    }

    BigDecimal amount = dto.getAmount();

    if (TransactionType.DEBIT.equals(dto.getTransactionType())) {
      if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BusinessException("Balance not available");
      }
      if (account.getCurrentBalance().compareTo(amount) < 0) {
        throw new BusinessException("Balance not available");
      }

      LocalDateTime startOfDate = LocalDate.now().atStartOfDay();
      LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
      BigDecimal todayDebits = transactionRepository.sumByAccountAndTypeAndDateRange(
          account.getAccountId(), TransactionType.DEBIT, startOfDate, endOfDay
      );
      BigDecimal newTotalDebits = todayDebits.add(amount);
      if (newTotalDebits.compareTo(BusinessConstants.DAILY_WITHDRAWAL_LIMIT) > 0) {
        throw new BusinessException("Daily quota exceeded");
      }

      amount = amount.negate();
      account.setCurrentBalance(account.getCurrentBalance().add(amount));
    } else {
      account.setCurrentBalance(account.getCurrentBalance().add(amount));
    }

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setDate(LocalDateTime.now());
    transaction.setTransactionType(dto.getTransactionType());
    transaction.setAmount(amount);
    transaction.setAvailableBalance(account.getCurrentBalance());

    accountRepository.save(account);
    Transaction newTransaction = transactionRepository.save(transaction);
    return transactionMapper.toResponseDTO(newTransaction);
  }

  @Override
  public Page<TransactionResponseDTO> findAll(String accountNumber, String clientName,
      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new BusinessException("Start date must be less than or equals than end date");
    }

    Page<Transaction> transactionList = transactionRepository.findByFilters(accountNumber,
        clientName, startDate, endDate, pageable);
    return transactionList.map(transactionMapper::toResponseDTO);
  }

  @Override
  public void delete(UUID id) {
    Transaction transaction = transactionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    Account account = transaction.getAccount();
    account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));

    accountRepository.save(account);
    transactionRepository.delete(transaction);
  }

}
