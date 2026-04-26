package com.management.app.cash_management_back.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

  public ResourceNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

}
