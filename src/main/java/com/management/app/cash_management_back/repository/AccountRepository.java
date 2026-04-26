package com.management.app.cash_management_back.repository;

import com.management.app.cash_management_back.entity.Account;
import com.management.app.cash_management_back.enums.AccountType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, UUID> {

  Optional<Account> findByAccountNumber(String accountNumber);
  boolean existsByAccountNumber(String accountNumber);
  boolean existsByClient_PersonId(UUID clientId);
  @Query("""
    SELECT a FROM Account a WHERE (:accountNumber IS NULL OR a.accountNumber LIKE %:accountNumber%) AND
    (:typeAccount IS NULL OR a.typeAccount = :typeAccount) AND
    (:clientName IS NULL OR LOWER(a.client.name) LIKE LOWER(CONCAT('%', :clientName, '%')))
  """)
  Page<Account> findByFilters(
      @Param("accountNumber") String accountNumber,
      @Param("typeAccount") AccountType typeAccount,
      @Param("clientName") String ClientName,
      Pageable pageable
  );

}
