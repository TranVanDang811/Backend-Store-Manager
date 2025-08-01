package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.request.UpdateSliderRequest;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.entity.*;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.SliderMapper;
import com.tranvandang.backend.repository.SliderRepository;
import com.tranvandang.backend.util.ChangerStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SliderService {
    SliderRepository sliderRepository;
    SliderMapper sliderMapper;
    CloudinaryService cloudinaryService;
    @PreAuthorize("hasRole('ADMIN')")
    public SliderResponse create(SliderRequest request, MultipartFile file) {
        Slider slider = sliderMapper.toSlider(request);

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        CloudinaryUploadResult result = cloudinaryService.uploadImage(file);
        if (result.getUrl() == null || result.getUrl().isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Images image = Images.builder()
                .slider(slider)
                .imageUrl(result.getUrl())
                .publicId(result.getPublicId())
                .createdAt(new Date())
                .build();

        slider.setImages(image);
        slider.setImageUrl(result.getUrl());
        slider = sliderRepository.save(slider);

        log.info("Saved slider: {}", slider);

        return sliderMapper.toSliderResponse(slider);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SliderResponse updateSlider(String sliderId, UpdateSliderRequest request) {
        Slider slider = sliderRepository.findById(sliderId)
                .orElseThrow(() -> new AppException(ErrorCode.SLIDER_NOT_FOUND));

        sliderMapper.updateSliderFromRequest(request, slider);

        Slider updatedSlider = sliderRepository.save(slider);

        return sliderMapper.toSliderResponse(updatedSlider);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<SliderResponse> getAll() {
        var sliders = sliderRepository.findAll();
        return sliders.stream().map(sliderMapper::toSliderResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SliderResponse getById(String id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SLIDER_NOT_FOUND));
        return sliderMapper.toSliderResponse(slider);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SliderResponse changerStatus(String sliderId, ChangerStatus status) {
        Slider slider = sliderRepository.findById(sliderId)
                .orElseThrow(() -> new AppException(ErrorCode.SLIDER_NOT_FOUND));

        slider.setStatus(status);

        return sliderMapper.toSliderResponse(sliderRepository.save(slider));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SLIDER_NOT_FOUND));

        Images image = slider.getImages();

        if (image != null && image.getPublicId() != null) {
            try {
                cloudinaryService.deleteImage(image.getPublicId());
            } catch (Exception e) {
                log.warn("Error deleting Cloudinary image: {}", image.getPublicId(), e);
            }
        }

        sliderRepository.delete(slider);
    }

}
