package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.CreateSupplierRequest;
import com.tranvandang.backend.dto.request.UpdateSupplierRequest;
import com.tranvandang.backend.dto.response.SupplierResponse;
import com.tranvandang.backend.service.SupplierService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SupplierResponse> create(@RequestBody CreateSupplierRequest request) {
        return ResponseEntity.ok(supplierService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAll() {
        return ResponseEntity.ok(supplierService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(supplierService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(
            @PathVariable String id,
            @RequestBody UpdateSupplierRequest request) {
        return ResponseEntity.ok(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

