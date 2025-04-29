package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.PaymentRequest;
import com.tranvandang.backend.dto.response.PaymentResponse;
import com.tranvandang.backend.service.PaymentService;
import com.tranvandang.backend.util.PaymentStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class    PaymentController {
    final PaymentService paymentService;

    /** Tạo thanh toán với phương thức được chọn */
    @PostMapping
    ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.createPayment(request))
                .build();
    }

    /** Xác nhận thanh toán qua chuyển khoản ngân hàng */
    @PostMapping("/confirm-bank-transfer")
    public ResponseEntity<ApiResponse<String>> confirmBankTransfer(@RequestParam String transactionId) {
        String message = paymentService.confirmBankTransfer(transactionId).getBody();
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .result(message)
                .build());
    }


    @PatchMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> changerStatus(
            @PathVariable String paymentId,
            @RequestParam PaymentStatus status) {
        PaymentResponse response = paymentService.changerStatus(paymentId, status);

        return ApiResponse.<PaymentResponse>builder()
                .result(response)
                .build();
    }

    /** Lấy danh sách tất cả các giao dịch thanh toán */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    /** Lấy thông tin thanh toán theo ID */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String paymentId) {
        PaymentResponse payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
