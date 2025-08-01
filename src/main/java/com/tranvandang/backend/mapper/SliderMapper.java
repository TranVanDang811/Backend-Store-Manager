package com.tranvandang.backend.mapper;



import com.tranvandang.backend.dto.request.ProductUpdateRequest;
import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.request.UpdateSliderRequest;
import com.tranvandang.backend.dto.response.ImagesResponse;
import com.tranvandang.backend.dto.response.ProductImageResponse;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.entity.Images;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.ProductImage;
import com.tranvandang.backend.entity.Slider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface SliderMapper {
    Slider toSlider(SliderRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
//    @Mapping(target = "images", source = "images")
    SliderResponse toSliderResponse(Slider slider);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl") // Dùng đúng tên thuộc tính
    ImagesResponse toImagesResponse(Images images);

    Set<ImagesResponse> toImagesResponses(Set<Images> images);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateSliderFromRequest(UpdateSliderRequest request, @MappingTarget Slider slider);
}
