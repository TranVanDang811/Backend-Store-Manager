package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.PaymentRequest;
import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.dto.response.PaymentResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.Payment;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.PaymentMapper;
import com.tranvandang.backend.repository.OrderRepository;
import com.tranvandang.backend.repository.PaymentRepository;
import com.tranvandang.backend.util.OrderStatus;
import com.tranvandang.backend.util.PaymentStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    final PaymentRepository paymentRepository;
    final OrderRepository orderRepository;
    final PaymentMapper paymentMapper;

//    @Value("${stripe.secret-key}")
//    private String stripeSecretKey;

    public PaymentResponse createPayment(PaymentRequest request) {
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);

        switch (request.getPaymentMethod()) {
            case CASH_ON_DELIVERY:
                return processCashOnDelivery(payment);
            case PAYPAL:
                return processPayPal(payment);
            case BANK_TRANSFER:
                return processBankTransfer(payment);
//            case CREDIT_CARD:
//                return processCreditCard(payment, request.getCardToken());
            default:
                throw new IllegalArgumentException("Unsupported payment method");
        }
    }

    /** Xử lý thanh toán khi nhận hàng */
    private PaymentResponse processCashOnDelivery(Payment payment) {
        payment.setAmount(payment.getOrder().getTotalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("COD-" + UUID.randomUUID());

        log.info("Processing COD payment: Order ID={}, Amount={}, Transaction ID={}",
                payment.getOrder().getId(), payment.getAmount(), payment.getTransactionId());

        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    /** Xử lý thanh toán qua PayPal */
    private PaymentResponse processPayPal(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("PAYPAL-" + UUID.randomUUID());

        boolean isPayPalConfirmed = verifyPayPalPayment(payment.getTransactionId());
        payment.setStatus(isPayPalConfirmed ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);

        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    /** Xử lý thanh toán qua chuyển khoản ngân hàng */
    private PaymentResponse processBankTransfer(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("BANK-" + UUID.randomUUID());

        // Trả về thông tin ngân hàng
        String bankInfo = "Ngân hàng: ABC Bank\n"
                + "Số tài khoản: 123456789\n"
                + "Chủ tài khoản: Công ty ABC\n"
                + "Nội dung chuyển khoản: THANHTOAN-" + payment.getTransactionId();

        System.out.println("Gửi thông tin ngân hàng cho khách: \n" + bankInfo);

        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    /** Xử lý thanh toán bằng thẻ tín dụng (Stripe) */
//    private PaymentResponse processCreditCard(Payment payment, String cardToken) {
//        try {
//            Stripe.apiKey = stripeSecretKey;
//
//            Map<String, Object> chargeParams = new HashMap<>();
//            chargeParams.put("amount", (int) (payment.getAmount() * 100)); // Stripe dùng cents
//            chargeParams.put("currency", "usd");
//            chargeParams.put("source", cardToken);
//            chargeParams.put("description", "Payment for order #" + payment.getOrder().getId());
//
//            Charge charge = Charge.create(chargeParams);
//
//            payment.setStatus(charge.getPaid() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
//            payment.setTransactionId(charge.getId());
//            paymentRepository.save(payment);
//
//            return paymentMapper.toResponse(payment);
//
//        } catch (StripeException e) {
//            throw new RuntimeException("Credit card payment failed: " + e.getMessage());
//        }
//    }

    /** API xác nhận chuyển khoản ngân hàng */
    public ResponseEntity<String> confirmBankTransfer(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            return ResponseEntity.ok("Payment confirmed successfully!");
        } else {
            return ResponseEntity.badRequest().body("Payment already processed or failed!");
        }
    }

    /** Xác minh thanh toán PayPal (giả lập) */
    private boolean verifyPayPalPayment(String transactionId) {
        // Giả lập xác nhận PayPal (có thể tích hợp PayPal API)
        return true; // Giả sử luôn thành công
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse changerStatus(String paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        payment.setStatus(status);

        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    /** Lấy tất cả thanh toán */
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    /** Lấy thanh toán theo ID */
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toResponse(payment);
    }

    /** Lấy thanh toán theo orderId */
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId: " + orderId));
        return paymentMapper.toResponse(payment);
    }
}
