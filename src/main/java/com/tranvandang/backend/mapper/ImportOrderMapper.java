package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.response.ImportOrderDetailResponse;
import com.tranvandang.backend.dto.response.ImportOrderResponse;
import com.tranvandang.backend.entity.ImportOrder;
import com.tranvandang.backend.entity.ImportOrderDetail;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ImportOrderMapper {

    // Mapping đơn nhập sang DTO
    @Mapping(target = "supplierId", source = "supplier.id", qualifiedByName = "mapUuidToString")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "importDetails", ignore = true) // custom bằng @AfterMapping
    ImportOrderResponse toResponse(ImportOrder order);

    List<ImportOrderResponse> toResponseList(List<ImportOrder> orders);

    // Mapping chi tiết đơn nhập
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    ImportOrderDetailResponse toDetailDto(ImportOrderDetail detail);

    List<ImportOrderDetailResponse> toDetailDtoList(List<ImportOrderDetail> details);

    // Custom sau khi ánh xạ xong
    @AfterMapping
    default void afterMapping(@MappingTarget ImportOrderResponse response, ImportOrder order) {
        response.setImportDetails(toDetailDtoList(order.getImportDetails()));
    }
    @Named("mapUuidToString")
    default String mapUuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
