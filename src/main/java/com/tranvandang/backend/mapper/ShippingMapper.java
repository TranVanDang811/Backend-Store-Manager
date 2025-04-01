package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.ShippingRequest;
import com.tranvandang.backend.dto.response.ShippingResponse;
import com.tranvandang.backend.entity.Shipping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ShippingMapper {
    ShippingMapper INSTANCE = Mappers.getMapper(ShippingMapper.class);

    @Mapping(target = "orderId", source = "order.id")
    ShippingResponse toResponse(Shipping shipping);

    @Mapping(target = "status", ignore = true)
    Shipping toEntity(ShippingRequest request);
}
