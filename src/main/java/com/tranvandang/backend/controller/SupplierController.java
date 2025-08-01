package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.CreateSupplierRequest;
import com.tranvandang.backend.dto.request.UpdateSupplierRequest;
import com.tranvandang.backend.dto.response.SupplierResponse;
import com.tranvandang.backend.service.SupplierService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class SupplierController {

    final SupplierService supplierService;

    @PostMapping
    ApiResponse<SupplierResponse> create(@RequestBody CreateSupplierRequest request) {
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<SupplierResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<Page<SupplierResponse>>builder().result(supplierService.getAll(page - 1, size)).build();
    }

    @GetMapping("/all")
    ApiResponse<List<SupplierResponse>> getAllNoPaging() {
        return ApiResponse.<List<SupplierResponse>>builder()
                .result(supplierService.getAllNoPaging())
                .build();
    }

    @GetMapping("/search")
    ApiResponse<Page<SupplierResponse>> searchByName(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<Page<SupplierResponse>>builder()
                .result(supplierService.searchByName(keyword, page - 1, size))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SupplierResponse> getById(@PathVariable String id) {
        return ApiResponse.<SupplierResponse>builder().result(supplierService.getById(id)).build();
    }

    @PutMapping("/{id}")
    ApiResponse<SupplierResponse> update(
            @PathVariable String id,
            @RequestBody UpdateSupplierRequest request) {
        return ApiResponse.<SupplierResponse>builder().result(supplierService.update(id, request)).build();    }

    @DeleteMapping("/{id}")
    ApiResponse<Void>delete(@PathVariable String id) {
        supplierService.delete(id);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }

    @GetMapping("/check-name")
    public ApiResponse<Boolean> checkName(@RequestParam String name) {
        return ApiResponse.<Boolean>builder()
                .result(supplierService.isSupplierNameExists(name))
                .build();
    }
}

