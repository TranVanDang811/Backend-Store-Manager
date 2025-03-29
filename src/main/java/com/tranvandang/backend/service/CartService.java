package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CartRequest;
import com.tranvandang.backend.dto.request.CartItemRequest;
import com.tranvandang.backend.dto.response.CartResponse;
import com.tranvandang.backend.entity.Cart;
import com.tranvandang.backend.entity.CartItem;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.User;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.CartMapper;
import com.tranvandang.backend.repository.CartItemRepository;
import com.tranvandang.backend.repository.CartRepository;
import com.tranvandang.backend.repository.ProductRepository;
import com.tranvandang.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse addToCart(CartItemRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, tăng số lượng
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Nếu chưa có, thêm mới
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        cartRepository.save(cart);
        return CartMapper.INSTANCE.toResponse(cart);
    }

    public CartResponse getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return CartMapper.INSTANCE.toResponse(cart);
    }

    @Transactional
    public void removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        boolean removed = cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        cartRepository.save(cart);
    }
}
