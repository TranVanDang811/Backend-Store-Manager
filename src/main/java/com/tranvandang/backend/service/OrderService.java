package com.tranvandang.backend.service;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tranvandang.backend.dto.request.OrderRequest;
import com.tranvandang.backend.dto.request.UpdateOrderQuantityRequest;
import com.tranvandang.backend.dto.response.DashboardStatsResponse;
import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.dto.response.RevenueStatsResponse;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.tranvandang.backend.entity.User;

import java.io.ByteArrayOutputStream;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.tranvandang.backend.entity.*;


import java.util.stream.Collectors;


import com.lowagie.text.*;


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
    final JavaMailSender mailSender;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Orders order = Orders.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        Orders savedOrder = orderRepository.save(order);

        Set<OrderDetail> orderDetails = request.getOrderDetails().stream()
                .map(detailRequest -> {
                    Product product = productRepository.findById(detailRequest.getProductId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

                    BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantity()));

                    return OrderDetail.builder()
                            .order(savedOrder)
                            .product(product)
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
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_DISCOUNT_CODE));

            if (discount.isCurrentlyValid()) {
                discountAmount = totalPrice.multiply(BigDecimal.valueOf(discount.getDiscountRate()));
                finalAmount = totalPrice.subtract(discountAmount);

                savedOrder.setDiscountCode(request.getDiscountCode());
            }
        }

        savedOrder.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        savedOrder.setFinalAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));

        orderDetailRepository.saveAll(orderDetails);
        savedOrder.setOrderDetails(orderDetails);

        orderRepository.saveAndFlush(order);
        sendOrderConfirmation(user, savedOrder);
        return orderMapper.toResponse(order);
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteOrder(String orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        orderRepository.delete(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Orders> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        List<Orders> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(String orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Transactional
    public OrderResponse updateOrderQuantity(String orderId, UpdateOrderQuantityRequest request) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_ORDER);
        }

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (OrderDetail detail : orderDetails) {
            Integer newQuantity = request.getQuantities().get(detail.getId());
            if (newQuantity != null) {
                if (newQuantity <= 0) {
                    throw new AppException(ErrorCode.INVALID_QUANTITY);
                }
                detail.setQuantity(newQuantity);
                detail.setTotalPrice(detail.getProductPrice().multiply(BigDecimal.valueOf(newQuantity)));
            }
        }

        BigDecimal newTotalPrice = orderDetails.stream()
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(newTotalPrice);

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = newTotalPrice;

        if (order.getDiscountCode() != null && !order.getDiscountCode().isEmpty()) {
            Discount discount = discountRepository.findByCode(order.getDiscountCode()).orElse(null);
            if (discount != null && discount.isCurrentlyValid()) {
                discountAmount = newTotalPrice.multiply(BigDecimal.valueOf(discount.getDiscountRate()));
                finalAmount = newTotalPrice.subtract(discountAmount);
            }
        }

        order.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        order.setFinalAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));

        return orderMapper.toResponse(order);
    }


    public void sendOrderConfirmation(User user, Orders order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Xác nhận đơn hàng #" + order.getId());
        message.setText("Cảm ơn bạn đã đặt hàng. Tổng tiền: " + order.getFinalAmount());

        mailSender.send(message);
    }

    public List<OrderResponse> getOrdersByDateRange(LocalDateTime start, LocalDateTime end , OrderStatus status) {
        List<Orders> orders = orderRepository.findAllByCreatedAtBetween(start, end);

        if (status != null) {
            orders = orders.stream()
                    .filter(order -> order.getStatus() == status)
                    .toList();
        }

        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public RevenueStatsResponse getRevenueStats(LocalDateTime start, LocalDateTime end) {
        List<Orders> orders = orderRepository.findAllByCreatedAtBetween(start, end).stream()
                .filter(order -> order.getStatus().name().equals("DELIVERED"))
                .toList();

        BigDecimal totalRevenue = orders.stream()
                .map(Orders::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalProductsSold = orders.stream()
                .flatMap(order -> order.getOrderDetails().stream())
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        return new RevenueStatsResponse((long) orders.size(), totalRevenue, totalProductsSold);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Orders> orders = orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsResponse getDashboardStats(LocalDateTime start, LocalDateTime end) {
        List<Orders> orders = orderRepository.findAllByCreatedAtBetween(start, end);

        long total = orders.size();
        long pending = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long delivered = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long canceled = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELED).count();

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Orders::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalProductsSold = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .flatMap(o -> o.getOrderDetails().stream())
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        return DashboardStatsResponse.builder()
                .totalOrders(total)
                .pendingOrders(pending)
                .deliveredOrders(delivered)
                .canceledOrders(canceled)
                .totalRevenue(totalRevenue)
                .totalProductsSold(totalProductsSold)
                .build();
    }

    public ResponseEntity<ByteArrayResource> printOrderInvoice(String orderId)  {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12);

        document.add(new Paragraph("HÓA ĐƠN ĐƠN HÀNG #" + order.getId(), titleFont));
        document.add(new Paragraph("Khách hàng: " + order.getUser().getUsername(), normalFont));
        document.add(new Paragraph("Email: " + order.getUser().getEmail(), normalFont));
        document.add(new Paragraph("Ngày đặt: " + order.getCreatedAt().toString(), normalFont));
        document.add(new Paragraph("Trạng thái: " + order.getStatus().name(), normalFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell("Tên sản phẩm");
        table.addCell("Giá");
        table.addCell("Số lượng");
        table.addCell("Thành tiền");

        for (OrderDetail detail : order.getOrderDetails()) {
            table.addCell(detail.getProduct().getName());
            table.addCell(detail.getProduct().getPrice().toString());
            table.addCell(String.valueOf(detail.getQuantity()));
            BigDecimal total = detail.getProduct().getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            table.addCell(total.toString());
        }

        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Tổng tiền: " + order.getFinalAmount().toString(), titleFont));
        document.close();

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}

