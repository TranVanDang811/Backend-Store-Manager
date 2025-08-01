package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.PaymentRequest;
import com.tranvandang.backend.dto.response.PaymentResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.Payment;
import com.tranvandang.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "processedBy", target = "processedBy", qualifiedByName = "mapUserToUsername")
    PaymentResponse toResponse(Payment payment);

    @Mapping(source = "orderId", target = "order", qualifiedByName = "mapOrderId")
    Payment toEntity(PaymentRequest request);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "processedBy", target = "processedBy", qualifiedByName = "mapUserToUsername")
    PaymentResponse toPaymentResponse(Payment payment);

    @Named("mapOrderId")
    static Orders mapOrderId(String orderId) {
        if (orderId == null) return null;
        Orders order = new Orders();
        order.setId(orderId);
        return order;
    }

    @Named("mapUserToUsername")
    static String mapUserToUsername(User user) {
        return user != null ? user.getUsername() : null;
    }
}
