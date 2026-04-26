package com.management.app.cash_management_back.controller;

import com.management.app.cash_management_back.dto.request.TransactionRequestDTO;
import com.management.app.cash_management_back.dto.response.TransactionResponseDTO;
import com.management.app.cash_management_back.service.TransactionService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  public ResponseEntity<Page<TransactionResponseDTO>> findAll(
      @RequestParam(required = false) String accountNumber,
      @RequestParam(required = false) String clientName,
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        transactionService.findAll(accountNumber, clientName, startDate, endDate, pageable));
  }

  @PostMapping
  public ResponseEntity<TransactionResponseDTO> create(
      @Valid @RequestBody TransactionRequestDTO dto) {
    return new ResponseEntity<>(transactionService.create(dto), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    transactionService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
