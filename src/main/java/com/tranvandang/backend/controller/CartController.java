package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.CartItemRequest;
import com.tranvandang.backend.dto.request.CartRequest;
import com.tranvandang.backend.dto.request.CategoryRequest;
import com.tranvandang.backend.dto.response.CartResponse;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/add")
    ApiResponse<CartResponse> addToCart(@PathVariable String userId, @RequestBody CartItemRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addToCart(request, userId))
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getCartByUserId(@PathVariable String userId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartByUserId(userId))
                .build();
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ApiResponse<Void> removeItemFromCart(@PathVariable String userId, @PathVariable String productId) {
        cartService.removeItemFromCart(userId, productId);
        return ApiResponse.<Void>builder()
                .message("Item removed successfully")
                .build();
    }
}
