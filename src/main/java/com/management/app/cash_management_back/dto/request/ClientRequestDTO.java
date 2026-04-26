package com.management.app.cash_management_back.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {

  @NotBlank(message = "Name is required")
  private String name;
  private String gender;
  @Min(value = 0, message = "Age must be positive")
  private Integer age;
  private String address;
  private String phone;
  @NotBlank(message = "ID Card is required")
  private String idCard;
  private String password;
  @NotNull
  private Boolean status;

}
