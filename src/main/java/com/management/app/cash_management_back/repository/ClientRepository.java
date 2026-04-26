package com.management.app.cash_management_back.repository;

import com.management.app.cash_management_back.entity.Client;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, UUID> {

  Optional<Client> findByIdCard(String idCard);
  boolean existsByIdCard(String idCard);
  Page<Client> findByNameContainingIgnoreCaseOrIdCardContainingIgnoreCase(
      String name, String idCard, Pageable pageable);


}
