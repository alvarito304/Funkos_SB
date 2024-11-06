package dev.alvaroherrero.funkosb.category.mapper;

import dev.alvaroherrero.funkosb.category.dto.CategoryDTO;
import dev.alvaroherrero.funkosb.category.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    // Mapper methods for converting between Category and CategoryDTO
    // ...
    // Example:
     public CategoryDTO toDTO(Category category) {
         CategoryDTO dto = new CategoryDTO();
         dto.setCategory(category.getCategory());
         // Other mapping properties
         return dto;
     }
      public Category toEntity(CategoryDTO dto) {
         Category category = new Category();
         category.setCategory(dto.getCategory());
         // Other mapping properties
         return category;
      }
}
