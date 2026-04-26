package com.management.app.cash_management_back.controller;

import com.management.app.cash_management_back.dto.request.ReportRequestDTO;
import com.management.app.cash_management_back.dto.response.ReportResponseDTO;
import com.management.app.cash_management_back.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping(value = "/transactions")
  public ResponseEntity<byte[]> generateTransactionReport(
      @Valid @RequestBody ReportRequestDTO request) {
    ReportResponseDTO reportResponseDTO = reportService.generateTransactionReport(request);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData(HttpHeaders.CONTENT_DISPOSITION, reportResponseDTO.getFileName());
    headers.setContentLength(reportResponseDTO.getContent().length);
    headers.setContentType(MediaType.APPLICATION_PDF);

    return ResponseEntity.ok().headers(headers).body(reportResponseDTO.getContent());
  }
}