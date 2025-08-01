package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.ImagesRequest;
import com.tranvandang.backend.dto.response.ImagesResponse;

import com.tranvandang.backend.entity.Images;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImagesMapper {

    @Mapping(target = "sliderId", source = "slider.id")
    @Mapping(target = "brandId", source = "brand.id")
    ImagesResponse toResponse(Images images);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "slider", ignore = true) // xử lý thủ công trong service
    @Mapping(target = "brand", ignore = true)
    Images toEntity(ImagesRequest request);
}
