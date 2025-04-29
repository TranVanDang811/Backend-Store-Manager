package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.response.CartItemResponse;
import com.tranvandang.backend.dto.response.CartResponse;
import com.tranvandang.backend.entity.Cart;
import com.tranvandang.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Đánh dấu interface này là một Mapper của MapStruct và Spring sẽ quản lý bean này
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class); // Tạo một instance của Mapper (không cần thiết khi dùng với Spring)

    @Mapping(source = "user.id", target = "userId") // Ánh xạ user.id của Cart entity sang userId trong CartResponse DTO
    @Mapping(source = "user.username", target = "userName") // Ánh xạ user.username sang userName
    @Mapping(source = "cartItems", target = "cartItems") // Ánh xạ danh sách CartItem từ entity sang DTO
    @Mapping(target = "totalAmount", expression = "java(cart.getCartItems().stream()"
            + ".map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))"
            + ".reduce(BigDecimal.ZERO, BigDecimal::add))") // Tính tổng tiền của giỏ hàng bằng cách lấy giá sản phẩm nhân số lượng rồi cộng dồn
    CartResponse toResponse(Cart cart); // Chuyển đổi từ Cart entity sang CartResponse DTO

    @Mapping(source = "product.id", target = "productId") // Ánh xạ product.id sang productId
    @Mapping(source = "product.name", target = "productName") // Ánh xạ product.name sang productName
    @Mapping(source = "product.price", target = "productPrice") // Ánh xạ product.price sang productPrice
    @Mapping(expression = "java(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))",
            target = "totalPrice") // Tính tổng giá của từng sản phẩm bằng cách lấy giá sản phẩm nhân số lượng
    CartItemResponse toCartItemResponse(CartItem cartItem); // Chuyển đổi từ CartItem entity sang CartItemResponse DTO

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems); // Chuyển đổi danh sách CartItem sang danh sách CartItemResponse
}
