package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.OrderDetailRequest;
import com.tranvandang.backend.dto.request.OrderRequest;
import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.entity.OrderDetail;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.OrderDetailMapper;
import com.tranvandang.backend.mapper.OrderMapper;
import com.tranvandang.backend.repository.OrderDetailRepository;
import com.tranvandang.backend.repository.OrderRepository;
import com.tranvandang.backend.repository.ProductRepository;
import com.tranvandang.backend.repository.UserRepository;
import com.tranvandang.backend.util.OrderStatus;
import com.tranvandang.backend.util.ProductStatus;
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
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Lấy thông tin user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo đơn hàng với trạng thái PENDING
        Orders order = Orders.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO) // Tính sau
                .build();

        // Lưu đơn hàng vào DB
        Orders savedOrder = orderRepository.save(order);

        // Xử lý danh sách sản phẩm trong đơn
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

        // Cập nhật tổng tiền đơn hàng
        BigDecimal totalPrice = orderDetails.stream()
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        savedOrder.setTotalPrice(totalPrice);

        // Lưu danh sách sản phẩm vào DB
        orderDetailRepository.saveAll(orderDetails);
        savedOrder.setOrderDetails(orderDetails);

        // Sử dụng MapStruct để chuyển đổi thành OrderResponse
        return orderMapper.toResponse(savedOrder);
    }


    public List<OrderResponse> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
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

