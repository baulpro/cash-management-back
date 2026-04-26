package com.management.app.cash_management_back.service;

import com.management.app.cash_management_back.dto.request.ClientRequestDTO;
import com.management.app.cash_management_back.dto.response.ClientResponseDTO;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

  Page<ClientResponseDTO> findAll(String search, Pageable pageable);

  ClientResponseDTO findById(UUID id);

  ClientResponseDTO create(ClientRequestDTO dto);

  ClientResponseDTO update(UUID id, ClientRequestDTO dto);

  void delete(UUID id);
}
