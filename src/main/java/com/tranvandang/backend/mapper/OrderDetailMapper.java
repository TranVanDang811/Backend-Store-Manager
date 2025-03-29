package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.OrderDetailRequest;
import com.tranvandang.backend.dto.response.OrderDetailResponse;
import com.tranvandang.backend.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;




@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "productPrice", source = "productPrice")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "totalPrice", source = "totalPrice")
    OrderDetailResponse toResponse(OrderDetail orderDetail);
}