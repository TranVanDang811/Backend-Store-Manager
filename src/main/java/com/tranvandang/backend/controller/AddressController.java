package com.tranvandang.backend.controller;


import com.tranvandang.backend.dto.request.AddressAddRequest;
import com.tranvandang.backend.dto.request.AddressUpdateRequest;
import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.response.AddressResponse;
import com.tranvandang.backend.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    private final AddressService addressService;

    // Created Address
    @PostMapping
    ApiResponse<AddressResponse> addAddress(@RequestBody AddressAddRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.addAddress(request))
                .build();
    }

    // Update Address
    @PutMapping("/{addressId}")
    ApiResponse<AddressResponse> updateAddress(@PathVariable String addressId, @RequestBody AddressUpdateRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(addressId, request))
                .build();
    }

    //Get address by id
    @GetMapping("/{id}")
    ApiResponse<AddressResponse> getById(@PathVariable String id) {
        return ApiResponse.<AddressResponse>builder().result(addressService.getAddressById(id)).build();
    }

    //Get address by user
    @GetMapping("/user/{userId}")
    ApiResponse<List<AddressResponse>> getAddressesByUserId(@PathVariable String userId) {
        return ApiResponse.<List<AddressResponse>>builder().result(addressService.getAddressesByUserId(userId)).build();
    }

    //Address default
    @GetMapping("/default")
    public AddressResponse getDefaultAddress(@RequestParam String userId) {
        return addressService.getDefaultAddressByUserId(userId);
    }

    // Endpoint set default address
    @PutMapping("/{id}/default")
    ApiResponse<Void> setDefaultAddress(
            @PathVariable("id") String addressId,
            @RequestParam("userId") String userId // hoặc lấy từ token
    ) {
        addressService.setDefaultAddress(userId, addressId);
        return ApiResponse.<Void>builder().message("Update default address successfully").build();
    }

    //Delete address
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }
}
