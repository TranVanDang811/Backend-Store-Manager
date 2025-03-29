package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.OrderRequest;
import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    OrderResponse toResponse(Orders order);

    OrderResponse toOrderResponse(Orders order);

}