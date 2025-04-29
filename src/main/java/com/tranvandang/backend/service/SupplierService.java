package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CreateSupplierRequest;
import com.tranvandang.backend.dto.request.UpdateSupplierRequest;
import com.tranvandang.backend.dto.response.SupplierResponse;
import com.tranvandang.backend.entity.Supplier;
import com.tranvandang.backend.mapper.SupplierMapper;
import com.tranvandang.backend.repository.SupplierRepository;
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
public class SupplierService {
    SupplierRepository supplierRepository;
    SupplierMapper supplierMapper;

    public SupplierResponse create(CreateSupplierRequest request) {
        Supplier supplier = supplierMapper.toEntity(request);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(supplier);
    }

    public List<SupplierResponse> getAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    public SupplierResponse getById(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return supplierMapper.toResponse(supplier);
    }

    public SupplierResponse update(String id, UpdateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplierMapper.updateSupplierFromDto(request, supplier);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(supplier);
    }

    public void delete(String id) {
        supplierRepository.deleteById(id);
    }
}
