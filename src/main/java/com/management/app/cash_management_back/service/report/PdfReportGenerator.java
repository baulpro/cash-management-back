package com.management.app.cash_management_back.service.report;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.app.cash_management_back.dto.business.AccountReportBusinessDTO;
import com.management.app.cash_management_back.exception.BusinessException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PdfReportGenerator implements ReportGenerator {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final Font TITLE_FONT =
      FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
  private static final Font SUBTITLE_FONT =
      FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
  private static final Font HEADER_FONT =
      FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
  private static final Font CELL_FONT =
      FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
  private static final Font INFO_FONT =
      FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);

  @Override
  public byte[] generate(String clientName, LocalDate startDate, LocalDate endDate,
      List<AccountReportBusinessDTO> data) {
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    try {
      PdfWriter.getInstance(document, outputStream);
      document.open();

      // Title
      Paragraph title = new Paragraph("Transaction Report", TITLE_FONT);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(20);
      document.add(title);

      // Report info
      document.add(new Paragraph("Client: " + clientName, INFO_FONT));
      document.add(new Paragraph(
          "Period: " + startDate.format(DATE_FORMATTER) +
              " to " + endDate.format(DATE_FORMATTER), INFO_FONT));
      document.add(new Paragraph(
          "Generated: " + LocalDate.now().format(DATE_FORMATTER), INFO_FONT));
      document.add(Chunk.NEWLINE);

      if (data.isEmpty()) {
        Paragraph noData = new Paragraph(
            "No transactions found for this client in the specified period.",
            INFO_FONT);
        noData.setAlignment(Element.ALIGN_CENTER);
        noData.setSpacingBefore(30);
        document.add(noData);
      } else {
        Paragraph tableTitle = new Paragraph("Account Summary", SUBTITLE_FONT);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        document.add(buildTable(data));

        document.add(Chunk.NEWLINE);
        document.add(buildTotalsSection(data));
      }

      document.close();
      return outputStream.toByteArray();

    } catch (DocumentException e) {
      throw new BusinessException("Error generating PDF report: " + e.getMessage());
    }
  }

  private PdfPTable buildTable(List<AccountReportBusinessDTO> data) throws DocumentException {
    PdfPTable table = new PdfPTable(7);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 1.5f, 1.5f, 1.5f, 1f, 1f, 1.5f});

    // Headers
    addHeaderCell(table, "Account Number");
    addHeaderCell(table, "Type");
    addHeaderCell(table, "Original Balance");
    addHeaderCell(table, "Current Balance");
    addHeaderCell(table, "# Deposits");
    addHeaderCell(table, "# Withdrawals");
    addHeaderCell(table, "Net Movement");

    // Data rows
    data.forEach(dto -> {
      addDataCell(table, dto.getAccountNumber());
      addDataCell(table, dto.getTypeAccount().name());
      addDataCell(table, "$ " + dto.getOriginalBalance().toPlainString());
      addDataCell(table, "$ " + dto.getCurrentBalance().toPlainString());
      addDataCell(table, String.valueOf(dto.getCreditCount()));
      addDataCell(table, String.valueOf(dto.getDebitCount()));
      BigDecimal net = dto.getTotalCredits().subtract(dto.getTotalDebits());
      addDataCell(table, "$ " + net.toPlainString());
    });

    return table;
  }

  private Element buildTotalsSection(List<AccountReportBusinessDTO> data) {
    BigDecimal totalCredits = data.stream()
        .map(AccountReportBusinessDTO::getTotalCredits)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalDebits = data.stream()
        .map(AccountReportBusinessDTO::getTotalDebits)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    long totalDeposits = data.stream()
        .mapToLong(AccountReportBusinessDTO::getCreditCount).sum();
    long totalWithdrawals = data.stream()
        .mapToLong(AccountReportBusinessDTO::getDebitCount).sum();

    PdfPTable totals = new PdfPTable(2);
    totals.setWidthPercentage(50);
    totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

    addInfoCell(totals, "Total Deposits:", String.valueOf(totalDeposits));
    addInfoCell(totals, "Total Withdrawals:", String.valueOf(totalWithdrawals));
    addInfoCell(totals, "Total Credits:", "$ " + totalCredits.toPlainString());
    addInfoCell(totals, "Total Debits:", "$ " + totalDebits.toPlainString());

    return totals;
  }

  private void addHeaderCell(PdfPTable table, String text) {
    PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
    cell.setBackgroundColor(new BaseColor(52, 73, 94));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setPadding(8);
    table.addCell(cell);
  }

  private void addDataCell(PdfPTable table, String text) {
    PdfPCell cell = new PdfPCell(new Phrase(text, CELL_FONT));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setPadding(6);
    table.addCell(cell);
  }

  private void addInfoCell(PdfPTable table, String label, String value) {
    PdfPCell labelCell = new PdfPCell(new Phrase(label, SUBTITLE_FONT));
    labelCell.setBorder(Rectangle.NO_BORDER);
    labelCell.setPadding(4);
    table.addCell(labelCell);

    PdfPCell valueCell = new PdfPCell(new Phrase(value, CELL_FONT));
    valueCell.setBorder(Rectangle.NO_BORDER);
    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    valueCell.setPadding(4);
    table.addCell(valueCell);
  }

}
