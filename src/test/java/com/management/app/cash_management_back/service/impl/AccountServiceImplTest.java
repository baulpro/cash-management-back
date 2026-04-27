package com.management.app.cash_management_back.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

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
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UUID clientId;
    private Client client;
    private Account account;
    private AccountRequestDTO request;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        client = new Client();
        client.setPersonId(clientId);
        client.setName("John Doe");
        client.setStatus(true);

        request = new AccountRequestDTO(
                "1234567890",
                AccountType.SAVING,
                BigDecimal.valueOf(500),
                true,
                clientId
        );

        account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setAccountNumber("1234567890");
        account.setTypeAccount(AccountType.SAVING);
        account.setOriginalBalance(BigDecimal.valueOf(500));
        account.setStatus(true);
    }

    @Test
    void create_shouldCreateAccountWhenAccountNumberDoesNotExistAndClientExists() {
        Account savedAccount = new Account();
        savedAccount.setAccountId(UUID.randomUUID());
        savedAccount.setAccountNumber("1234567890");
        savedAccount.setTypeAccount(AccountType.SAVING);
        savedAccount.setOriginalBalance(BigDecimal.valueOf(500));
        savedAccount.setCurrentBalance(BigDecimal.valueOf(500));
        savedAccount.setStatus(true);
        savedAccount.setClient(client);

        AccountResponseDTO expectedResponse = AccountResponseDTO.builder()
                .accountId(savedAccount.getAccountId())
                .accountNumber("1234567890")
                .typeAccount(AccountType.SAVING)
                .originalBalance(BigDecimal.valueOf(500))
                .currentBalance(BigDecimal.valueOf(500))
                .status(true)
                .clientId(clientId)
                .clientName("John Doe")
                .build();

        when(accountRepository.existsByAccountNumber("1234567890"))
                .thenReturn(false);
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));
        when(accountMapper.toEntity(request))
                .thenReturn(account);
        when(accountRepository.save(account))
                .thenReturn(savedAccount);
        when(accountMapper.toResponseDTO(savedAccount))
                .thenReturn(expectedResponse);

        AccountResponseDTO result = accountService.create(request);

        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        assertEquals(AccountType.SAVING, result.getTypeAccount());
        assertEquals(0, result.getCurrentBalance().compareTo(BigDecimal.valueOf(500)));
        assertEquals(clientId, result.getClientId());

        verify(accountRepository).existsByAccountNumber("1234567890");
        verify(clientRepository).findById(clientId);
        verify(accountMapper).toEntity(request);
        verify(accountRepository).save(account);
        verify(accountMapper).toResponseDTO(savedAccount);
    }

    @Test
    void create_shouldThrowBusinessExceptionWhenAccountNumberAlreadyExists() {
        when(accountRepository.existsByAccountNumber("1234567890"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.create(request)
        );

        assertEquals("account 1234567890 already exists", exception.getMessage());

        verify(accountRepository).existsByAccountNumber("1234567890");
        verify(clientRepository, never()).findById(any(UUID.class));
        verify(accountMapper, never()).toEntity(any(AccountRequestDTO.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void create_shouldThrowResourceNotFoundExceptionWhenClientDoesNotExist() {
        when(accountRepository.existsByAccountNumber("1234567890"))
                .thenReturn(false);
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.create(request)
        );

        assertEquals("client " + clientId + " not found", exception.getMessage());

        verify(accountRepository).existsByAccountNumber("1234567890");
        verify(clientRepository).findById(clientId);
        verify(accountMapper, never()).toEntity(any(AccountRequestDTO.class));
        verify(accountRepository, never()).save(any(Account.class));
    }
}