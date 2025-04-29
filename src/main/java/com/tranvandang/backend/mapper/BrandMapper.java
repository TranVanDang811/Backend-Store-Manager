package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.BrandRequest;
import com.tranvandang.backend.dto.response.BrandResponse;

import com.tranvandang.backend.entity.Brand;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand(BrandRequest request);

    BrandResponse toBrandResponse(Brand brand);
}
