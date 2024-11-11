package dev.alvaroherrero.funkosb.category.mapper;

import dev.alvaroherrero.funkosb.category.dto.CategoryDTO;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private CategoryMapper categoryMapper = new CategoryMapper();

    Category category = Category.builder()
            .category(FunkoCategory.SERIE)
            .build();

    CategoryDTO categoryDTO = new CategoryDTO(category.getCategory());

    @Test
    void toDTO() {
        CategoryDTO actual = categoryMapper.toDTO(category);
        assertEquals(categoryDTO, actual);
    }

    @Test
    void toEntity() {
        Category actual = categoryMapper.toEntity(categoryDTO);
        assertEquals(category.getCategory(), actual.getCategory());
    }
}