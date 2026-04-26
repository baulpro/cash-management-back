package com.management.app.cash_management_back.mapper;

import com.management.app.cash_management_back.dto.request.AccountRequestDTO;
import com.management.app.cash_management_back.dto.response.AccountResponseDTO;
import com.management.app.cash_management_back.entity.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  @Mapping(source = "client.personId", target = "clientId")
  @Mapping(source = "client.name", target = "clientName")
  AccountResponseDTO toResponseDTO(Account account);

  @Mapping(target = "accountId", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "currentBalance", ignore = true)
  Account toEntity(AccountRequestDTO dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "accountId", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "currentBalance", ignore = true)
  @Mapping(target = "originalBalance", ignore = true)
  void updateEntityFromDTO(AccountRequestDTO dto, @MappingTarget Account account);

}
