package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.OrderRequest;
import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.OrderDetail;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.OrderDetailMapper;
import com.tranvandang.backend.mapper.OrderMapper;
import com.tranvandang.backend.repository.*;
import com.tranvandang.backend.util.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.tranvandang.backend.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import com.tranvandang.backend.entity.*;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    final OrderRepository orderRepository;
    final OrderDetailRepository orderDetailRepository;
    final UserRepository userRepository;
    final ProductRepository productRepository;
    final OrderMapper orderMapper;
    final OrderDetailMapper orderDetailMapper;
    final DiscountRepository discountRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders order = Orders.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        Orders savedOrder = orderRepository.save(order);

        Set<OrderDetail> orderDetails = request.getOrderDetails().stream()
                .map(detailRequest -> {
                    Product product = productRepository.findById(detailRequest.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantity()));

                    return OrderDetail.builder()
                            .order(savedOrder)
                            .productId(product.getId())
                            .productName(product.getName())
                            .productPrice(product.getPrice())
                            .quantity(detailRequest.getQuantity())
                            .totalPrice(totalPrice)
                            .build();
                })
                .collect(Collectors.toSet());

        BigDecimal totalPrice = orderDetails.stream()
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        savedOrder.setTotalPrice(totalPrice);

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = totalPrice;

        if (request.getDiscountCode() != null && !request.getDiscountCode().isEmpty()) {
            Discount discount = discountRepository.findByCode(request.getDiscountCode())
                    .orElseThrow(() -> new RuntimeException("Invalid discount code"));

            if (discount.isCurrentlyValid()) {
                discountAmount = totalPrice.multiply(BigDecimal.valueOf(discount.getDiscountRate()));
                finalAmount = totalPrice.subtract(discountAmount);
            }
        }

        savedOrder.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        savedOrder.setFinalAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));

        orderDetailRepository.saveAll(orderDetails);
        savedOrder.setOrderDetails(orderDetails);

        return orderMapper.toResponse(savedOrder);
    }



    public List<OrderResponse> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(String orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        return orderMapper.toResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse changerStatus(String orderId, OrderStatus status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        order.setStatus(status);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}

