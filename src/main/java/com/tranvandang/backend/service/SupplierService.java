package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CreateSupplierRequest;
import com.tranvandang.backend.dto.request.UpdateSupplierRequest;
import com.tranvandang.backend.dto.response.SupplierResponse;
import com.tranvandang.backend.entity.Supplier;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.SupplierMapper;
import com.tranvandang.backend.repository.SupplierRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierService {
    SupplierRepository supplierRepository;
    SupplierMapper supplierMapper;
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponse create(CreateSupplierRequest request) {
        if (isSupplierNameExists(request.getName())) {
            throw new AppException(ErrorCode.SUPPLIER_NAME_ALREADY_EXISTS);
        }
        Supplier supplier = supplierMapper.toEntity(request);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(supplier);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<SupplierResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<SupplierResponse> getAllNoPaging() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponse getById(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        return supplierMapper.toResponse(supplier);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponse update(String id, UpdateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        if (!supplier.getName().equalsIgnoreCase(request.getName()) &&
                isSupplierNameExists(request.getName())) {
            throw new AppException(ErrorCode.SUPPLIER_NAME_ALREADY_EXISTS);
        }

        supplierMapper.updateSupplierFromDto(request, supplier);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(supplier);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        supplierRepository.delete(supplier);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<SupplierResponse> searchByName(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page, size);
        return supplierRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(supplierMapper::toResponse);
    }

    public boolean isSupplierNameExists(String name) {
        return supplierRepository.existsByNameIgnoreCase(name);
    }
}
