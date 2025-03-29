package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.AddressAddRequest;
import com.tranvandang.backend.dto.request.AddressUpdateRequest;
import com.tranvandang.backend.entity.Address;
import com.tranvandang.backend.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    private final AddressService addressService;

    // Thêm Address
    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody AddressAddRequest request) {
        Address savedAddress = addressService.addAddress(request);
        return ResponseEntity.ok(savedAddress);
    }

    // Cập nhật Address
    @PutMapping
    public ResponseEntity<Address> updateAddress(@RequestBody AddressUpdateRequest request) {
        Address updatedAddress = addressService.updateAddress(request);
        return ResponseEntity.ok(updatedAddress);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUserId(@PathVariable String userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }
}
