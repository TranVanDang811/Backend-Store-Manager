package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.PaymentRequest;
import com.tranvandang.backend.dto.response.PaymentResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);

    @Mapping(source = "orderId", target = "order", qualifiedByName = "mapOrderId")
    Payment toEntity(PaymentRequest request);

    PaymentResponse toPaymentResponse(Payment payment);

    @Named("mapOrderId")
    static Orders mapOrderId(String orderId) {
        if (orderId == null) return null;
        Orders order = new Orders();
        order.setId(orderId);
        return order;
    }
}
