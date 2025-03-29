package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CategoryRequest;
import com.tranvandang.backend.dto.request.PermissionRequest;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.dto.response.PermissionResponse;
import com.tranvandang.backend.entity.Category;
import com.tranvandang.backend.entity.Permission;
import com.tranvandang.backend.mapper.CategoryMapper;
import com.tranvandang.backend.repository.CategoryRepository;
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
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getAll() {
        var categorys = categoryRepository.findAll();
        return categorys.stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public void delete(String category) {
        categoryRepository.deleteById(category);
    }
}
