package com.management.app.cash_management_back.service.impl;

import com.management.app.cash_management_back.dto.business.AccountReportBusinessDTO;
import com.management.app.cash_management_back.dto.request.ReportRequestDTO;
import com.management.app.cash_management_back.dto.response.ReportResponseDTO;
import com.management.app.cash_management_back.entity.Account;
import com.management.app.cash_management_back.entity.Transaction;
import com.management.app.cash_management_back.enums.TransactionType;
import com.management.app.cash_management_back.exception.BusinessException;
import com.management.app.cash_management_back.repository.TransactionRepository;
import com.management.app.cash_management_back.service.ReportService;
import com.management.app.cash_management_back.service.report.ReportGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

  private final TransactionRepository transactionRepository;
  private final ReportGenerator reportGenerator;

  @Override
  public ReportResponseDTO generateTransactionReport(ReportRequestDTO request) {
    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new BusinessException("Start date must be less than or equal to end date");
    }

    LocalDateTime start = request.getStartDate().atStartOfDay();
    LocalDateTime end = request.getEndDate().atTime(LocalTime.MAX);

    List<Transaction> transactions = transactionRepository.findForReport(
        request.getClientName(), start, end);

    List<AccountReportBusinessDTO> reportData = aggregateByAccount(transactions);

    byte[] pdfBytes = reportGenerator.generate(
        request.getClientName(),
        request.getStartDate(),
        request.getEndDate(),
        reportData);

    String fileName = String.format("transaction_report_%s_%s_%s.pdf",
        request.getClientName().replaceAll("\\s+", "_"),
        request.getStartDate(),
        request.getEndDate());

    return ReportResponseDTO.builder()
        .fileName(fileName)
        .content(pdfBytes)
        .build();
  }

  private List<AccountReportBusinessDTO> aggregateByAccount(List<Transaction> transactions) {
    Map<Account, List<Transaction>> grouped = transactions.stream()
        .collect(Collectors.groupingBy(Transaction::getAccount));

    return grouped.entrySet().stream()
        .map(entry -> buildAccountReport(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(AccountReportBusinessDTO::getAccountNumber))
        .toList();
  }

  private AccountReportBusinessDTO buildAccountReport(Account account,
      List<Transaction> txs) {
    long creditCount = txs.stream()
        .filter(t -> t.getTransactionType() == TransactionType.CREDIT)
        .count();

    long debitCount = txs.stream()
        .filter(t -> t.getTransactionType() == TransactionType.DEBIT)
        .count();

    BigDecimal totalCredits = txs.stream()
        .filter(t -> t.getTransactionType() == TransactionType.CREDIT)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalDebits = txs.stream()
        .filter(t -> t.getTransactionType() == TransactionType.DEBIT)
        .map(t -> t.getAmount().abs())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return AccountReportBusinessDTO.builder()
        .accountNumber(account.getAccountNumber())
        .typeAccount(account.getTypeAccount())
        .originalBalance(account.getOriginalBalance())
        .currentBalance(account.getCurrentBalance())
        .creditCount(creditCount)
        .debitCount(debitCount)
        .totalCredits(totalCredits)
        .totalDebits(totalDebits)
        .build();
  }

}
