package com.management.app.cash_management_back.service;

import com.management.app.cash_management_back.dto.request.ReportRequestDTO;
import com.management.app.cash_management_back.dto.response.ReportResponseDTO;

public interface ReportService {

  ReportResponseDTO generateTransactionReport(ReportRequestDTO request);
}