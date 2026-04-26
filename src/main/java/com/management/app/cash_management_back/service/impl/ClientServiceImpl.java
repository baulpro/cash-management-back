package com.management.app.cash_management_back.service.impl;

import com.management.app.cash_management_back.dto.request.ClientRequestDTO;
import com.management.app.cash_management_back.dto.response.ClientResponseDTO;
import com.management.app.cash_management_back.entity.Client;
import com.management.app.cash_management_back.exception.BusinessException;
import com.management.app.cash_management_back.exception.ResourceNotFoundException;
import com.management.app.cash_management_back.mapper.ClientMapper;
import com.management.app.cash_management_back.repository.AccountRepository;
import com.management.app.cash_management_back.repository.ClientRepository;
import com.management.app.cash_management_back.service.ClientService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;
  private final AccountRepository accountRepository;
  private final ClientMapper clientMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<ClientResponseDTO> findAll(String search, Pageable pageable) {
    Page<Client> clients = (StringUtils.hasText(search)) ?
        clientRepository.findByNameContainingIgnoreCaseOrIdCardContainingIgnoreCase(
            search, search, pageable
        ) :
        clientRepository.findAll(pageable);

    return clients.map(clientMapper::toResponseDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public ClientResponseDTO findById(UUID id) {
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("client not found with id: " + id));
    return clientMapper.toResponseDTO(client);
  }

  @Override
  public ClientResponseDTO create(ClientRequestDTO dto) {
    if (clientRepository.existsByIdCard(dto.getIdCard())) {
      throw new BusinessException("client with idCard " + dto.getIdCard() +  " already exists");
    }
    Client client = clientMapper.toEntity(dto);
    Client newClient = clientRepository.save(client);
    return clientMapper.toResponseDTO(newClient);
  }

  @Override
  public ClientResponseDTO update(UUID id, ClientRequestDTO dto) {
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("client not found with id: " + id));
    clientRepository.findByIdCard(dto.getIdCard())
        .filter(existingClient -> !existingClient.getPersonId().equals(id))
        .ifPresent(_ -> {
          throw new BusinessException("client with idCard " + dto.getIdCard() +  "already exists");
        });
    clientMapper.updateEntityFromDTO(dto, client);
    Client updatedClient = clientRepository.save(client);
    return clientMapper.toResponseDTO(updatedClient);
  }

  @Override
  public void delete(UUID id) {
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("client not found with id: " + id));
    if (accountRepository.existsByClient_PersonId(id)) {
      throw new  BusinessException("Cannot delete client with existing accounts");
    }
    clientRepository.delete(client);
  }
}
