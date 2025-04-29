package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.CreateSupplierRequest;
import com.tranvandang.backend.dto.request.UpdateSupplierRequest;
import com.tranvandang.backend.dto.response.SupplierResponse;
import com.tranvandang.backend.entity.Supplier;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierResponse toResponse(Supplier supplier);

    Supplier toEntity(CreateSupplierRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSupplierFromDto(UpdateSupplierRequest request, @MappingTarget Supplier supplier);
}