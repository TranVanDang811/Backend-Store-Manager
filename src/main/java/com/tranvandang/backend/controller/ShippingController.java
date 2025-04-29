package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ShippingRequest;
import com.tranvandang.backend.dto.response.ShippingResponse;
import com.tranvandang.backend.service.ShippingService;
import com.tranvandang.backend.util.ShippingStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shippings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShippingController {
    private final ShippingService shippingService;

    // 1️⃣ Tạo đơn vận chuyển
    @PostMapping
    public ResponseEntity<ShippingResponse> createShipping(@RequestBody ShippingRequest request) {
        return ResponseEntity.ok(shippingService.createShipping(request));
    }

    // 2️⃣ Lấy thông tin đơn vận chuyển theo ID
    @GetMapping("/{shippingId}")
    public ResponseEntity<ShippingResponse> getShippingById(@PathVariable String shippingId) {
        return ResponseEntity.ok(shippingService.getShippingById(shippingId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponse> getShippingByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(shippingService.getShippingByOrderId(orderId));
    }

    // 3️⃣ Lấy danh sách đơn vận chuyển (có phân trang)
    @GetMapping
    public ResponseEntity<Page<ShippingResponse>> getAllShippings(Pageable pageable) {
        return ResponseEntity.ok(shippingService.getAllShippings(pageable));
    }

    // 4️⃣ Cập nhật trạng thái vận chuyển
    @PatchMapping("/{shippingId}")
    public ResponseEntity<ShippingResponse> updateShippingStatus(
            @PathVariable String shippingId,
            @RequestParam ShippingStatus status) {
        return ResponseEntity.ok(shippingService.updateShippingStatus(shippingId, status));
    }

    // 5️⃣ Cập nhật mã theo dõi vận chuyển
    @PatchMapping("/{shippingId}/tracking-number")
    public ResponseEntity<ShippingResponse> updateTrackingNumber(
            @PathVariable String shippingId,
            @RequestParam String trackingNumber) {
        return ResponseEntity.ok(shippingService.updateTrackingNumber(shippingId, trackingNumber));
    }

    // 6️⃣ Xóa đơn vận chuyển (chỉ khi chưa giao hàng)
    @DeleteMapping("/{shippingId}")
    public ResponseEntity<Void> deleteShipping(@PathVariable String shippingId) {
        shippingService.deleteShipping(shippingId);
        return ResponseEntity.noContent().build();
    }
}
