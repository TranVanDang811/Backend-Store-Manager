package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.CreateImportOrderRequest;
import com.tranvandang.backend.dto.response.ImportOrderResponse;
import com.tranvandang.backend.service.ImportOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/import-orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportOrderController {
    ImportOrderService importOrderService;

    // 🧾 Tạo mới phiếu nhập hàng
    @PostMapping
    public ResponseEntity<ImportOrderResponse> createImportOrder(@RequestBody CreateImportOrderRequest request) {
        ImportOrderResponse response = importOrderService.createImportOrder(request);
        return ResponseEntity.ok(response);
    }

    // Xác nhận đơn nhập kho
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
            @RequestParam(required = false) String status,             // DRAFT | IMPORTED
            @RequestParam(required = false) String sortByImportDate     // ASC | DESC
    ) {
        Page<ImportOrderResponse> orders = importOrderService.getImportOrders(
                status, sortByImportDate, page - 1, size
        );

        return ApiResponse.<Page<ImportOrderResponse>>builder()
                .result(orders)
                .build();
    }


    // 🔍 Lấy chi tiết phiếu nhập theo ID
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
}
