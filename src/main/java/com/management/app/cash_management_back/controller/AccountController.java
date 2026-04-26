package com.management.app.cash_management_back.controller;

import com.management.app.cash_management_back.dto.groups.CreateValidation;
import com.management.app.cash_management_back.dto.groups.UpdateValidation;
import com.management.app.cash_management_back.dto.request.AccountRequestDTO;
import com.management.app.cash_management_back.dto.response.AccountResponseDTO;
import com.management.app.cash_management_back.enums.AccountType;
import com.management.app.cash_management_back.service.AccountService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<Page<AccountResponseDTO>> findAll(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) AccountType typeAccount,
            @RequestParam(required = false) String clientName,
            Pageable pageable) {
        return ResponseEntity.ok(
                accountService.findAll(accountNumber, typeAccount, clientName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(
            @Validated(CreateValidation.class) @RequestBody AccountRequestDTO dto) {
        return new ResponseEntity<>(accountService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> update(
            @PathVariable UUID id,
            @Validated(UpdateValidation.class) @RequestBody AccountRequestDTO dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}