package com.tranvandang.backend.entity;

import com.tranvandang.backend.util.PaymentMethod;
import com.tranvandang.backend.util.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Payment extends AbstractEntity{
    @Column(nullable = false, unique = true, length = 50)
    String transactionId; // Mã giao dịch riêng (TXN-...)

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod; // VISA, PayPal, COD...

    @Enumerated(EnumType.STRING)
    PaymentStatus status; // PENDING, SUCCESS, FAILED


    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

}
