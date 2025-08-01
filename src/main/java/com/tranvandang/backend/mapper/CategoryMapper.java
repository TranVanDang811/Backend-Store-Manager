package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.CategoryRequest;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);

    @Mapping(target = "id", source = "id")
    CategoryResponse toCategoryResponse(Category category);
}
