package com.management.app.cash_management_back.service;

import com.management.app.cash_management_back.dto.request.TransactionRequestDTO;
import com.management.app.cash_management_back.dto.response.TransactionResponseDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

  TransactionResponseDTO create(TransactionRequestDTO dto);
  Page<TransactionResponseDTO> findAll(String accountNumber, String clientName,
                                                  LocalDateTime startDate, LocalDateTime endDate,
                                                  Pageable pageable);
  void delete(UUID id);

}
