package com.management.app.cash_management_back.mapper;

import com.management.app.cash_management_back.dto.response.TransactionResponseDTO;
import com.management.app.cash_management_back.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "account.accountNumber", target = "accountNumber")
  @Mapping(source = "account.client.name", target = "clientName")
  TransactionResponseDTO toResponseDTO(Transaction transaction);

}
