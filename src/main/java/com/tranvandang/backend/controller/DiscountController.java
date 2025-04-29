package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.DiscountRequest;
import com.tranvandang.backend.dto.response.DiscountResponse;
import com.tranvandang.backend.service.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountController {
    final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountResponse> createDiscount(@RequestBody DiscountRequest request) {
        DiscountResponse discount = discountService.createDiscount(request);
        return ResponseEntity.ok(discount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountResponse> updateDiscount(@PathVariable String id,
                                                           @RequestBody DiscountRequest request) {
        DiscountResponse discount = discountService.updateDiscount(id, request);
        return ResponseEntity.ok(discount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponse> getDiscountById(@PathVariable String id) {
        DiscountResponse discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(discount);
    }

    @GetMapping
    public ResponseEntity<List<DiscountResponse>> getAllDiscounts() {
        List<DiscountResponse> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }
}
