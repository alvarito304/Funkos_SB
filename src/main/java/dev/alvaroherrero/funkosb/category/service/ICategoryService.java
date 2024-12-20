package dev.alvaroherrero.funkosb.category.service;

import dev.alvaroherrero.funkosb.category.model.Category;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(UUID id);
    Category createCategory(Category category);
    Category updateCategory(UUID id, Category category);
    Category deleteCategory(UUID id);
    Category softDeleteCategory(UUID id);
}
