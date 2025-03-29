package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.response.CartItemResponse;
import com.tranvandang.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cartItem))")  // Tính tổng giá
    CartItemResponse toResponse(CartItem cartItem);

    default BigDecimal calculateTotalPrice(CartItem cartItem) {
        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
