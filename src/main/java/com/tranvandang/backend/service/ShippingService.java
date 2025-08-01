package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.ShippingRequest;
import com.tranvandang.backend.dto.response.ShippingResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.Shipping;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.ShippingMapper;
import com.tranvandang.backend.repository.OrderRepository;
import com.tranvandang.backend.repository.ShippingRepository;
import com.tranvandang.backend.util.ShippingStatus;
import com.tranvandang.backend.util.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final ShippingMapper shippingMapper;

    @Transactional
    public ShippingResponse createShipping(ShippingRequest request) {
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals(OrderStatus.PROCESSING)) {
            throw new AppException(ErrorCode.ORDER_NOT_CONFIRMED);
        }

        Shipping shipping = shippingMapper.toEntity(request);
        shipping.setOrder(order);
        shipping.setStatus(ShippingStatus.PENDING);

        Shipping savedShipping = shippingRepository.save(shipping);
        return shippingMapper.toResponse(savedShipping);
    }

    public ShippingResponse getShippingById(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_NOT_FOUND));
        return shippingMapper.toResponse(shipping);
    }

    public ShippingResponse getShippingByOrderId(String orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping not found for orderId: " + orderId));
        return shippingMapper.toResponse(shipping);
    }

    public Page<ShippingResponse> getAllShippings(Pageable pageable) {
        return shippingRepository.findAll(pageable)
                .map(shippingMapper::toResponse);
    }

    @Transactional
    public ShippingResponse updateShippingStatus(String shippingId, ShippingStatus newStatus) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_NOT_FOUND));

        shipping.setStatus(newStatus);
        shipping.setUpdateAt(LocalDateTime.now());

        return shippingMapper.toResponse(shippingRepository.save(shipping));
    }

    @Transactional
    public ShippingResponse updateTrackingNumber(String shippingId, String trackingNumber) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_NOT_FOUND));

        shipping.setTrackingNumber(trackingNumber);
        shipping.setUpdateAt(LocalDateTime.now());

        return shippingMapper.toResponse(shippingRepository.save(shipping));
    }

    @Transactional
    public void deleteShipping(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_NOT_FOUND));

        if (!shipping.getStatus().equals(ShippingStatus.PENDING)) {
            throw new AppException(ErrorCode.ORDER_ALREADY_SHIPPED);
        }

        shippingRepository.delete(shipping);
    }
}
