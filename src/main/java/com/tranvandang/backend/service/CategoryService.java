package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CategoryRequest;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.entity.Category;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.CategoryMapper;
import com.tranvandang.backend.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse update(String categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse getById(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<CategoryResponse> getPagedCategories(Pageable pageable, String keyword) {
        Page<Category> categories = categoryRepository
                .findByNameContainingIgnoreCase(keyword, pageable);
        return categories.map(categoryMapper::toCategoryResponse);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryResponse> getAll() {
        var categorys = categoryRepository.findAll();
        return categorys.stream().map(categoryMapper::toCategoryResponse).toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String category) {
        categoryRepository.deleteById(category);
    }
}
