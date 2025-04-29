package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.CategoryRequest;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.entity.Category;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);
}
