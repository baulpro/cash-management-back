package com.management.app.cash_management_back.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDTO {

  private UUID clientId;
  private String name;
  private String gender;
  private Integer age;
  private String address;
  private String phone;
  private String idCard;
  private Boolean status;

}
