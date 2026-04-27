package com.management.app.cash_management_back.service.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.management.app.cash_management_back.dto.request.TransactionRequestDTO;
import com.management.app.cash_management_back.dto.response.TransactionResponseDTO;
import com.management.app.cash_management_back.entity.Account;
import com.management.app.cash_management_back.entity.Client;
import com.management.app.cash_management_back.entity.Transaction;
import com.management.app.cash_management_back.enums.AccountType;
import com.management.app.cash_management_back.enums.TransactionType;
import com.management.app.cash_management_back.exception.BusinessException;
import com.management.app.cash_management_back.mapper.TransactionMapper;
import com.management.app.cash_management_back.repository.AccountRepository;
import com.management.app.cash_management_back.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID accountId;
    private Account account;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();

        Client client = new Client();
        client.setStatus(true);
        client.setName("John Doe");

        account = new Account();
        account.setAccountId(accountId);
        account.setAccountNumber("1234567890");
        account.setTypeAccount(AccountType.SAVING);
        account.setOriginalBalance(BigDecimal.valueOf(1500));
        account.setCurrentBalance(BigDecimal.valueOf(1500));
        account.setStatus(true);
        account.setClient(client);
    }

    @Test
    void create_shouldCreateDebitTransactionWhenBalanceAndDailyQuotaAreValid() {
        TransactionRequestDTO request = new TransactionRequestDTO(
                "1234567890",
                TransactionType.DEBIT,
                BigDecimal.valueOf(200)
        );

        Transaction savedTransaction = new Transaction();
        savedTransaction.setTransactionId(UUID.randomUUID());
        savedTransaction.setAccount(account);
        savedTransaction.setDate(LocalDateTime.now());
        savedTransaction.setTransactionType(TransactionType.DEBIT);
        savedTransaction.setAmount(BigDecimal.valueOf(-200));
        savedTransaction.setAvailableBalance(BigDecimal.valueOf(1300));

        TransactionResponseDTO expectedResponse = TransactionResponseDTO.builder()
                .transactionId(savedTransaction.getTransactionId())
                .accountNumber("1234567890")
                .transactionType(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(-200))
                .availableBalance(BigDecimal.valueOf(1300))
                .clientName("John Doe")
                .build();

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.sumByAccountAndTypeAndDateRange(
                eq(accountId),
                eq(TransactionType.DEBIT),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(BigDecimal.valueOf(300));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);
        when(transactionMapper.toResponseDTO(savedTransaction))
                .thenReturn(expectedResponse);

        TransactionResponseDTO result = transactionService.create(request);

        assertNotNull(result);
        assertEquals(TransactionType.DEBIT, result.getTransactionType());
        assertEquals(BigDecimal.valueOf(-200), result.getAmount());
        assertEquals(BigDecimal.valueOf(1300), result.getAvailableBalance());
        assertEquals(BigDecimal.valueOf(1300), account.getCurrentBalance());

        verify(accountRepository).findByAccountNumber("1234567890");
        verify(transactionRepository).sumByAccountAndTypeAndDateRange(
                eq(accountId),
                eq(TransactionType.DEBIT),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionMapper).toResponseDTO(savedTransaction);
    }

    @Test
    void create_shouldThrowBusinessExceptionWhenDailyQuotaIsExceeded() {
        TransactionRequestDTO request = new TransactionRequestDTO(
                "1234567890",
                TransactionType.DEBIT,
                BigDecimal.valueOf(200)
        );

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.sumByAccountAndTypeAndDateRange(
                eq(accountId),
                eq(TransactionType.DEBIT),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(BigDecimal.valueOf(900));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.create(request)
        );

        assertEquals("Daily quota exceeded", exception.getMessage());
        assertEquals(BigDecimal.valueOf(1500), account.getCurrentBalance());

        verify(accountRepository).findByAccountNumber("1234567890");
        verify(transactionRepository).sumByAccountAndTypeAndDateRange(
                eq(accountId),
                eq(TransactionType.DEBIT),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionMapper, never()).toResponseDTO(any(Transaction.class));
    }
}