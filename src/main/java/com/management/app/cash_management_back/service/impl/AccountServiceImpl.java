package com.management.app.cash_management_back.service.impl;

import com.management.app.cash_management_back.dto.request.AccountRequestDTO;
import com.management.app.cash_management_back.dto.response.AccountResponseDTO;
import com.management.app.cash_management_back.entity.Account;
import com.management.app.cash_management_back.entity.Client;
import com.management.app.cash_management_back.enums.AccountType;
import com.management.app.cash_management_back.exception.BusinessException;
import com.management.app.cash_management_back.exception.ResourceNotFoundException;
import com.management.app.cash_management_back.mapper.AccountMapper;
import com.management.app.cash_management_back.repository.AccountRepository;
import com.management.app.cash_management_back.repository.ClientRepository;
import com.management.app.cash_management_back.repository.TransactionRepository;
import com.management.app.cash_management_back.service.AccountService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

  private final ClientRepository clientRepository;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final AccountMapper accountMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<AccountResponseDTO> findAll(String accountNumber, AccountType accountType,
      String clientName, Pageable pageable) {
    Page<Account> accounts = accountRepository.findByFilters(accountNumber, accountType, clientName,
        pageable);
    return accounts.map(accountMapper::toResponseDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public AccountResponseDTO findById(UUID id) {
    Account account = accountRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("account not found with id: " + id));
    return accountMapper.toResponseDTO(account);
  }

  @Override
  public AccountResponseDTO create(AccountRequestDTO dto) {
    if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
      throw new BusinessException("account " + dto.getAccountNumber() + " already exists");
    }

    Client client = clientRepository.findById(dto.getClientId())
        .orElseThrow(() -> new ResourceNotFoundException("client " + dto.getClientId() + " not found"));

    Account account = accountMapper.toEntity(dto);
    account.setClient(client);
    account.setCurrentBalance(dto.getOriginalBalance());

    Account newAccount = accountRepository.save(account);
    return accountMapper.toResponseDTO(newAccount);
  }

  @Override
  public AccountResponseDTO update(UUID id, AccountRequestDTO dto) {
    Account account = accountRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("account not found with id: " + id));

    accountRepository.findByAccountNumber(dto.getAccountNumber())
        .filter(existingAccount -> !existingAccount.getAccountId().equals(id))
        .ifPresent(_ -> {
          throw new BusinessException("account " + dto.getAccountNumber() + " already exists");
        });

    if (!account.getClient().getPersonId().equals(dto.getClientId())) {
      Client newClient = clientRepository.findById(dto.getClientId())
          .orElseThrow(() -> new ResourceNotFoundException("client " + dto.getClientId() + " not found"));
      account.setClient(newClient);
    }

    accountMapper.updateEntityFromDTO(dto, account);
    Account updatedAccount = accountRepository.save(account);
    return accountMapper.toResponseDTO(updatedAccount);
  }

  @Override
  public void delete(UUID id) {
    Account account = accountRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("account not found with id: " + id));

    if (transactionRepository.existsByAccount_AccountId(id)) {
      throw new BusinessException("Cannot delete an account that has transactions");
    }

    accountRepository.delete(account);
  }
}
