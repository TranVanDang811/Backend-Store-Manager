package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.DiscountRequest;
import com.tranvandang.backend.dto.response.DiscountResponse;
import com.tranvandang.backend.entity.Discount;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.exception.ResourceNotFoundException;
import com.tranvandang.backend.mapper.DiscountMapper;
import com.tranvandang.backend.repository.DiscountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountService {
    final DiscountRepository discountRepository;
    final DiscountMapper discountMapper;

    private boolean isActive(LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now();
        return startDate != null && (startDate.isBefore(now) || startDate.isEqual(now));
    }

    public DiscountResponse createDiscount(DiscountRequest request) {
        // Tạo entity từ request
        Discount discount = new Discount(); //

        // Sao chép thủ công các trường cơ bản
        discount.setName(request.getName());
        discount.setDiscountRate(request.getDiscountRate());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setCode(request.getCode());

        // Thiết lập active dựa trên logic của bạn
        discount.setActive(isActive(discount.getStartDate()));

        log.info("Active status before save: {}", discount.isActive());
        Discount savedDiscount = discountRepository.save(discount);
        log.info("Active status after save: {}", savedDiscount.isActive());

        return discountMapper.toDiscountResponse(savedDiscount);
    }



    public DiscountResponse updateDiscount(String id, DiscountRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DISCOUNT_NOT_FOUND_MESSAGE  + id));

        // Cập nhật thông tin discount từ request
        discountMapper.updateDiscountFromRequest(request, discount);

        // Cập nhật trạng thái active sau khi thay đổi thời gian
        discount.setActive(isActive(discount.getStartDate()));

        // Lưu lại discount sau khi cập nhật
        Discount updatedDiscount = discountRepository.save(discount);

        // Trả về response
        return discountMapper.toDiscountResponse(updatedDiscount);
    }


    public void deleteDiscount(String id) {
        if (!discountRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.DISCOUNT_NOT_FOUND_MESSAGE + id);
        }
        discountRepository.deleteById(id);
    }

    public DiscountResponse getDiscountById(String id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DISCOUNT_NOT_FOUND_MESSAGE + id));
        return discountMapper.toDiscountResponse(discount);
    }

    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll()
                .stream()
                .map(discountMapper::toDiscountResponse)
                .toList(); // Replaced collect with toList()
    }
}


