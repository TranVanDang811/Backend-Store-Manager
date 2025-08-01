package com.tranvandang.backend.service;


import com.cloudinary.api.exceptions.ApiException;
import com.tranvandang.backend.entity.ImportOrder;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ImportOrderExportService {

    ImportOrderRepository importOrderRepository;

    public ByteArrayInputStream exportImportOrdersToExcel() {
        List<ImportOrder> orders = importOrderRepository.findAll(); // Hoặc dùng filter if needed

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Import Orders");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"Order ID", "Supplier Name", "Import Date", "Status", "Total Amount"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            int rowIdx = 1;
            for (ImportOrder order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getSupplier().getName());
                row.createCell(2).setCellValue(order.getImportDate().format(formatter));
                row.createCell(3).setCellValue(order.getStatus().name());
                row.createCell(4).setCellValue(order.getTotalPrice().doubleValue());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
