package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.OrderRequest;
import com.tranvandang.backend.dto.request.UpdateOrderQuantityRequest;

import com.tranvandang.backend.dto.response.DashboardStatsResponse;
import com.tranvandang.backend.dto.response.OrderResponse;
import com.tranvandang.backend.dto.response.RevenueStatsResponse;
import com.tranvandang.backend.service.OrderService;
import com.tranvandang.backend.util.OrderStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    //Create order
    @PostMapping
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    //Order List
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders()).build();
    }

    //List of orders by id
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
    }

    //Update status
    @PatchMapping("/{orderId}")
    public ApiResponse<OrderResponse> changerStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        OrderResponse response = orderService.changerStatus(orderId, status);

        return ApiResponse.<OrderResponse>builder()
                .result(response)
                .build();
    }

    //Delete order
    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }

    //List of user orders
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> getOrdersByUser(@PathVariable String userId) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrdersByUserId(userId)).build();
    }

    //List of status
    @GetMapping("/status")
    public ApiResponse<List<OrderResponse>> getOrdersByStatus(@RequestParam OrderStatus status) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrdersByStatus(status)).build();
    }

    //Cancel order
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.cancelOrder(orderId))
                .build();
    }

    //Update qty
    @PutMapping("/{orderId}/update-quantities")
    public ApiResponse<OrderResponse> updateOrderQuantities(
            @PathVariable String orderId,
            @RequestBody UpdateOrderQuantityRequest request
    ) {
        OrderResponse updatedOrder = orderService.updateOrderQuantity(orderId, request);
        return ApiResponse.<OrderResponse>builder()
                .result(updatedOrder)
                .build();
    }

    //List of orders by start end date and status
    @GetMapping("/filter")
    public List<OrderResponse> filterByDateRange(  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                   @RequestParam(value = "status", required = false) OrderStatus status) {
        return orderService.getOrdersByDateRange(start, end,status);
    }

    //Revenue
    @GetMapping("/revenue")
    public RevenueStatsResponse revenueStats(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return orderService.getRevenueStats(start, end);
    }

    //Print check
    @GetMapping("/print/{orderId}")
    public ResponseEntity<ByteArrayResource> printOrder(@PathVariable String orderId) throws IOException {
        return orderService.printOrderInvoice(orderId);
    }

    //Logged in user orders
    @GetMapping("/my-orders")
    public ApiResponse<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders(authentication))
                .build();
    }

    //Sales statistics
    @GetMapping("/dashboard-stats")
    public ApiResponse<DashboardStatsResponse> getDashboardStats(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ApiResponse.<DashboardStatsResponse>builder()
                .result(orderService.getDashboardStats(start, end))
                .build();
    }

}
