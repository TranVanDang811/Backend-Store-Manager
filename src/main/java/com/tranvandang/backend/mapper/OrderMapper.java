package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class})
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "orderDetails", source = "orderDetails")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "discountCode", source = "discountCode")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updateAt", source = "updateAt")
    OrderResponse toResponse(Orders order);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderDetails", target = "orderDetails")
    OrderResponse toOrderResponse(Orders order);

}