package com.management.app.cash_management_back.service.report;

import com.management.app.cash_management_back.dto.business.AccountReportBusinessDTO;
import java.time.LocalDate;
import java.util.List;

public interface ReportGenerator {

  byte[] generate(String clientName, LocalDate startDate, LocalDate endDate,
      List<AccountReportBusinessDTO> data);

}
