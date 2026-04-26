package com.management.app.cash_management_back.mapper;

import com.management.app.cash_management_back.dto.request.ClientRequestDTO;
import com.management.app.cash_management_back.dto.response.ClientResponseDTO;
import com.management.app.cash_management_back.entity.Client;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClientMapper {

  Client toEntity(ClientRequestDTO dto);
  @Mapping(source = "personId", target = "clientId")
  ClientResponseDTO toResponseDTO(Client client);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDTO(ClientRequestDTO dto, @MappingTarget Client client);

}
