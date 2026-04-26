package com.management.app.cash_management_back.service;

import com.management.app.cash_management_back.dto.request.AccountRequestDTO;
import com.management.app.cash_management_back.dto.response.AccountResponseDTO;
import com.management.app.cash_management_back.enums.AccountType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

  Page<AccountResponseDTO> findAll(String accountNumber, AccountType accountType,
      String clientName, Pageable pageable);

  AccountResponseDTO findById(UUID id);

  AccountResponseDTO create(AccountRequestDTO dto);

  AccountResponseDTO update(UUID id, AccountRequestDTO dto);

  void delete(UUID id);
}
