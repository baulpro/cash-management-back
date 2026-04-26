package com.management.app.cash_management_back.controller;

import com.management.app.cash_management_back.dto.request.ClientRequestDTO;
import com.management.app.cash_management_back.dto.response.ClientResponseDTO;
import com.management.app.cash_management_back.service.ClientService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  public ResponseEntity<Page<ClientResponseDTO>> findAll(
      @RequestParam(required = false) String search,
      Pageable pageable) {
    return ResponseEntity.ok(clientService.findAll(search, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientResponseDTO> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(clientService.findById(id));
  }

  @PostMapping
  public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientRequestDTO dto) {
    return new ResponseEntity<>(clientService.create(dto), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClientResponseDTO> update(@PathVariable UUID id,
      @Valid @RequestBody ClientRequestDTO dto) {
    return ResponseEntity.ok(clientService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    clientService.delete(id);
    return ResponseEntity.noContent().build();
  }
}