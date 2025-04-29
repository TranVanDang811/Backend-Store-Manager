package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    OrderResponse toResponse(Orders order);

    @Mapping(source = "user.id", target = "userId")
    OrderResponse toOrderResponse(Orders order);

}