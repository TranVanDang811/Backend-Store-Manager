package com.tranvandang.backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tranvandang.backend.dto.request.PaymentRequest;
import com.tranvandang.backend.dto.response.PaymentResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.Payment;
import com.tranvandang.backend.entity.User;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.PaymentMapper;
import com.tranvandang.backend.repository.OrderRepository;
import com.tranvandang.backend.repository.PaymentRepository;
import com.tranvandang.backend.repository.UserRepository;
import com.tranvandang.backend.util.PaymentMethod;
import com.tranvandang.backend.util.PaymentStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    final PaymentRepository paymentRepository;
    final OrderRepository orderRepository;
    final PaymentMapper paymentMapper;
    final UserRepository userRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setTransactionId(generateTransactionId(request.getPaymentMethod()));
        payment.setAmount(order.getTotalPrice());
        payment.setCreatedAt(LocalDateTime.now());

        if (request.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY ) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProcessedBy(currentUser);
            payment.setProcessedAt(LocalDateTime.now());
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            payment.setStatus(PaymentStatus.PENDING);
            payment.setProcessedBy(currentUser);
            payment.setProcessedAt(LocalDateTime.now());
        }

        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }


    private String generateTransactionId(PaymentMethod method) {
        return method.name() + "-" + UUID.randomUUID();
    }

    public String confirmBankTransfer(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return "The transaction has been processed.";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setProcessedBy(currentUser);
        payment.setProcessedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        return "Confirm successful transfer.";
    }

    /** Get all payments */
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    /** Get paid by ID */
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toResponse(payment);
    }

    /** Get payment by orderId */
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId: " + orderId));
        return paymentMapper.toResponse(payment);
    }


    //----------------------

    private String generateQrContent(Orders order) {
        return String.format(
                "Ngân hàng: Vietcombank\n" +
                        "Số TK: 0123456789\n" +
                        "Chủ TK: TRAN VAN DANG\n" +
                        "Nội dung: THANHTOAN-%s\n" +
                        "Số tiền: %,.0f VND",
                order.getId(),
                order.getTotalPrice()
        );
    }

    public byte[] generateQrImage(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new AppException(ErrorCode.UNABLE_QRCODE);
        }
    }

    public byte[] getPaymentQrByOrderId(String orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        String content = generateQrContent(order);
        return generateQrImage(content);
    }
}
