package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.AddressAddRequest;
import com.tranvandang.backend.dto.request.AddressUpdateRequest;
import com.tranvandang.backend.dto.response.AddressResponse;
import com.tranvandang.backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(Address address);

    @Mapping(target = "id", ignore = true) // ID tự động tạo
    @Mapping(target = "user", ignore = true) // Gán sau trong Service
    Address toEntity(AddressAddRequest addressRequest);

    @Mapping(target = "id", ignore = true) // Không cập nhật ID
    void updateAddressFromDto(AddressUpdateRequest dto, @MappingTarget Address entity);

    @Named("toResponseSet")
    default Set<AddressResponse> toResponseSet(Set<Address> addresses) {
        if (addresses == null) return new HashSet<>();
        return addresses.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }

    @Named("toEntitySet")
    default Set<Address> toEntitySet(Set<AddressAddRequest> addressRequests) {
        if (addressRequests == null) return new HashSet<>();
        return addressRequests.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}

