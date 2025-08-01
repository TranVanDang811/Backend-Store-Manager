package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.CategoryRequest;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categorys")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    //Created category
    @PostMapping
    ApiResponse<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.create(request))
                .build();
    }

    //Update category
    @PutMapping("/{id}")
    ApiResponse<CategoryResponse> update(
            @PathVariable String id,
            @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder().result(categoryService.update(id, request)).build();
    }

    //Get category by id
    @GetMapping("/{id}")
    ApiResponse<CategoryResponse> getById(@PathVariable String id) {
        return ApiResponse.<CategoryResponse>builder().result(categoryService.getById(id)).build();
    }

    //Get all category by search
    @GetMapping("/search")
    public ApiResponse<Page<CategoryResponse>> getPagedCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<CategoryResponse> resultPage = categoryService.getPagedCategories(pageable, keyword);

        return ApiResponse.<Page<CategoryResponse>>builder()
                .result(resultPage)
                .build();
    }

    //Get all category
    @GetMapping
    ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAll())
                .build();
    }

    //Delete category
    @DeleteMapping("/{category}")
    ApiResponse<Void> delete(@PathVariable String category) {
        categoryService.delete(category);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }
}
