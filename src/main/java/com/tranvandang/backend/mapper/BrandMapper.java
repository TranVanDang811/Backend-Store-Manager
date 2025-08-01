package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.BrandRequest;
import com.tranvandang.backend.dto.request.BrandUpdateRequest;
import com.tranvandang.backend.dto.request.UpdateSliderRequest;
import com.tranvandang.backend.dto.response.BrandResponse;

import com.tranvandang.backend.dto.response.ImagesResponse;
import com.tranvandang.backend.entity.Brand;

import com.tranvandang.backend.entity.Images;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand toBrand(BrandRequest request);

    @Mapping(target = "imageUrl", source = "imageUrl")
    BrandResponse toBrandResponse(Brand brand);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl") // Dùng đúng tên thuộc tính
    ImagesResponse toImagesResponse(Images images);



    @Mapping(target = "status", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateBrandFromRequest(BrandUpdateRequest request, @MappingTarget Brand brand);
}
