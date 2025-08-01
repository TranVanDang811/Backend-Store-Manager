package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.AddressAddRequest;
import com.tranvandang.backend.dto.request.AddressUpdateRequest;
import com.tranvandang.backend.dto.response.AddressResponse;
import com.tranvandang.backend.entity.Address;
import com.tranvandang.backend.entity.User;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
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

    // Created Address
    public AddressResponse addAddress(AddressAddRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return addressMapper.toResponse(savedAddress);
    }

    // Update Address
    public AddressResponse updateAddress(String addressId, AddressUpdateRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        addressMapper.updateAddressFromDto(request, address);


        Address updateAddress = addressRepository.save(address);

        return addressMapper.toResponse(updateAddress);

    }

    public AddressResponse getAddressById(String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        return addressMapper.toResponse(address);
    }

    public List<AddressResponse> getAddressesByUserId(String userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    public AddressResponse getDefaultAddressByUserId(String userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        return addressMapper.toResponse(address);
    }


    public void setDefaultAddress(String userId, String addressId) {

        Address addressToSet = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));


        if (!addressToSet.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ADDRESS_NOT_BELONG_TO_USER);
        }


        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(addr -> {
            if (addr.isDefault()) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        });


        addressToSet.setDefault(true);
        addressRepository.save(addressToSet);
    }


    // Delete address
    public void deleteAddress(String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        addressRepository.delete(address);
    }
}
