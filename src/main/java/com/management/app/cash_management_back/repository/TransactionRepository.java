package com.management.app.cash_management_back.repository;

import com.management.app.cash_management_back.entity.Transaction;
import com.management.app.cash_management_back.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

  boolean existsByAccount_AccountId(UUID accountId);

  List<Transaction> findByAccount_Client_NameContainingIgnoreCaseAndDateBetween(String ClientName,
      LocalDateTime start, LocalDateTime end);

  @Query("""
        SELECT COALESCE(SUM(ABS(t.amount)), 0) FROM Transaction t
        WHERE t.account.accountId = :accountId AND t.transactionType = :type
        AND t.date BETWEEN :start and :end
      """)
  BigDecimal sumByAccountAndTypeAndDateRange(
      @Param("accountId") UUID accountId,
      @Param("type") TransactionType type,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end
  );

  @Query("""
      SELECT t FROM Transaction t WHERE
      (:accountNumber IS NULL OR t.account.accountNumber LIKE %:accountNumber%) AND
      (:clientName IS NULL OR LOWER(t.account.client.name) LIKE LOWER(CONCAT('%', :clientName, '%'))) AND
      (:startDate IS NULL OR t.date >= :startDate) AND
      (:endDate IS NULL OR t.date <= :endDate)
      """)
  Page<Transaction> findByFilters(
      @Param("accountNumber") String accountNumber,
      @Param("clientName") String clientName,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

  @Query("""
      SELECT t FROM Transaction t
      WHERE LOWER(t.account.client.name) LIKE LOWER(CONCAT('%', :clientName, '%'))
      AND t.date BETWEEN :startDate AND :endDate
      ORDER BY t.account.accountNumber, t.date
      """)
  List<Transaction> findForReport(@Param("clientName") String clientName,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

}
