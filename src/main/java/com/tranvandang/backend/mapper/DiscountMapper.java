package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.DiscountRequest;
import com.tranvandang.backend.dto.response.DiscountResponse;
import com.tranvandang.backend.entity.Discount;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    DiscountResponse toDiscountResponse(Discount discount);

//    @Mapping(target = "active", ignore = true) // Bỏ qua trường active khi map
    Discount toDiscount(DiscountRequest createDiscountRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDiscountFromRequest(DiscountRequest updateRequest, @MappingTarget Discount discount);
}

