package com.tranvandang.backend.service;



import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.entity.Slider;
import com.tranvandang.backend.mapper.SliderMapper;
import com.tranvandang.backend.repository.SliderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SliderService {
    SliderRepository sliderRepository;
    SliderMapper sliderMapper;
    CloudinaryService cloudinaryService;
    public SliderResponse create(SliderRequest request, MultipartFile image) {
        Slider slider = sliderMapper.toSlider(request);

        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                slider.setImageUrl(imageUrl);
            }
        }

        slider = sliderRepository.save(slider);
        log.info("Saved slider: {}", slider);

        return sliderMapper.toSliderResponse(slider);
    }

    public List<SliderResponse> getAll() {
        var sliders = sliderRepository.findAll();
        return sliders.stream().map(sliderMapper::toSliderResponse).toList();
    }



    public void delete(String slider) {
        sliderRepository.deleteById(slider);
    }
}
