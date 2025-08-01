package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.PaymentRequest;
import com.tranvandang.backend.dto.response.PaymentResponse;
import com.tranvandang.backend.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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


    //Create payment with selected method
    @PostMapping
    ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.createPayment(request))
                .build();
    }


    /** Confirm transfer with transactionId */
    @PostMapping("/confirm/{transactionId}")
    public ResponseEntity<String> confirmBankTransfer(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.confirmBankTransfer(transactionId));
    }

    @GetMapping("/qr/{orderId}")
    public ResponseEntity<byte[]> getPaymentQr(@PathVariable String orderId) {
        byte[] qrImage = paymentService.getPaymentQrByOrderId(orderId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(qrImage);
    }

    /** Get a list of all payment transactions */
    @GetMapping
    public ApiResponse<List<PaymentResponse>> getAllPayments() {
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(paymentService.getAllPayments())
                .build();
    }

    /** Get payment information by ID */
    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> getPaymentById(@PathVariable String paymentId) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.getPaymentById(paymentId))
                .build();
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.getPaymentByOrderId(orderId))
                .build();
    }
}
