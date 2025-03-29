package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.AddressAddRequest;
import com.tranvandang.backend.dto.request.AddressUpdateRequest;
import com.tranvandang.backend.entity.Address;
import com.tranvandang.backend.entity.User;
import com.tranvandang.backend.mapper.AddressMapper;
import com.tranvandang.backend.repository.AddressRepository;
import com.tranvandang.backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    // Thêm mới Address
    public Address addAddress(AddressAddRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Address address = addressMapper.toEntity(request);
        address.setUser(user); // Gán user vào entity

        return addressRepository.save(address);
    }

    // Cập nhật Address
    public Address updateAddress(AddressUpdateRequest request) {
        Address address = addressRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Address không tồn tại"));

        addressMapper.updateAddressFromDto(request, address); // Dùng MapStruct để cập nhật

        return addressRepository.save(address);
    }

    public List<Address> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId);
    }
}
