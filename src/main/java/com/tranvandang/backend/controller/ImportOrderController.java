package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.CreateImportOrderRequest;
import com.tranvandang.backend.dto.request.UpdateImportOrderRequest;
import com.tranvandang.backend.dto.response.ImportOrderResponse;
import com.tranvandang.backend.dto.response.ImportOrderStatisticResponse;
import com.tranvandang.backend.service.ImportOrderExportService;
import com.tranvandang.backend.service.ImportOrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/import-orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportOrderController {
    ImportOrderService importOrderService;
    ImportOrderExportService importOrderExportService;

    // Create new receipt
    @PostMapping
    public ResponseEntity<ImportOrderResponse> createImportOrder(@RequestBody CreateImportOrderRequest request) {
        ImportOrderResponse response = importOrderService.createImportOrder(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ImportOrderResponse> updateImportOrder(
            @PathVariable String orderId,
            @RequestBody @Valid UpdateImportOrderRequest request) {
        return ResponseEntity.ok(importOrderService.updateImportOrder(orderId, request));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ImportOrderStatisticResponse> getImportStatistics(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ImportOrderStatisticResponse response = importOrderService.getStatistics(from, to);
        return ResponseEntity.ok(response);
    }


    // Confirm warehouse receipt
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmImportOrder(@PathVariable String orderId) {
        importOrderService.confirmImportOrder(orderId);
        return ResponseEntity.ok("Import order confirmed successfully.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<ImportOrderResponse>> getImportOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String status,             // DRAFT | IMPORTED
            @RequestParam(required = false) String sortByImportDate     // ASC | DESC
    ) {
        Page<ImportOrderResponse> orders = importOrderService.getImportOrders(
                status, sortByImportDate, supplierName, page - 1, size
        );

        return ApiResponse.<Page<ImportOrderResponse>>builder()
                .result(orders)
                .build();
    }


    // Get entry details by ID
    @GetMapping("/{id}")
    public ResponseEntity<ImportOrderResponse> getById(@PathVariable String id) {
        ImportOrderResponse response = importOrderService.getById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteImportOrder(@PathVariable String orderId) {
        try {
            importOrderService.deleteImportOrder(orderId);
            return ApiResponse.<Void>builder().message("Delete import order successfully").build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder().message("Failed to delete import order").build();
        }
    }

    //--------------
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportImportOrdersToExcel() {
        ByteArrayInputStream in = importOrderExportService.exportImportOrdersToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=import-orders.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

}
