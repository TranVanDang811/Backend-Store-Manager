package com.tranvandang.backend.mapper;



import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.entity.Slider;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SliderMapper {
    Slider toSlider(SliderRequest request);

    SliderResponse toSliderResponse(Slider slider);
}
