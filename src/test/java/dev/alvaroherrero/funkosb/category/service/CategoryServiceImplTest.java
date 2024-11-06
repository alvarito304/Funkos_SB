package dev.alvaroherrero.funkosb.category.service;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.repository.ICategoryRepository;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
import dev.alvaroherrero.funkosb.funko.service.FunkoServiceImpl;
import dev.alvaroherrero.funkosb.funko.storage.service.IStorageService;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private IFunkoRepository funkoRepository;

    @Mock
    private ICategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Funko funkoTest;
    private Category categoryTest;

    @BeforeEach
    void setUp() {

        categoryTest = new Category();
        categoryTest.setId(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        categoryTest.setCategory(FunkoCategory.SERIE);

        funkoTest = new Funko();
        funkoTest.setName("Test Funko");
        funkoTest.setPrice(10.0F);
        funkoTest.setCategory(categoryTest);

    }

    @Test
    void getAllCategories() {
        when(categoryRepository.findAllActiveCategories()).thenReturn(List.of(categoryTest));
        var result = categoryService.getAllCategories();
        assertAll(() -> assertEquals(1, result.size()), () -> assertEquals(categoryTest.getCategory(), result.getFirst().getCategory()));
        verify(categoryRepository, times(1)).findAllActiveCategories();
    }

    @Test
    void getCategoryById() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.ofNullable(categoryTest));
        var result = categoryService.getCategoryById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        assertAll(
                () -> assertEquals(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"), result.getId()),
                () -> assertEquals(categoryTest.getCategory(), result.getCategory()));
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }

    @Test
    void createCategory() {
        when(categoryRepository.save(categoryTest)).thenReturn(categoryTest);
        var result = categoryService.createCategory(categoryTest);
        assertAll(
                () -> assertEquals(categoryTest.getId(), result.getId()),
                () -> assertEquals(categoryTest.getCategory(), result.getCategory()));
        verify(categoryRepository, times(1)).save(categoryTest);
    }

    @Test
    void updateCategory() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.ofNullable(categoryTest));
        when(categoryRepository.save(categoryTest)).thenReturn(categoryTest);
        var result = categoryService.updateCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"), categoryTest);
        assertAll(
                () -> assertEquals(categoryTest.getId(), result.getId()),
                () -> assertEquals(categoryTest.getCategory(), result.getCategory()));

        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        verify(categoryRepository, times(1)).save(categoryTest);
    }

    @Test
    void deleteCategory() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.ofNullable(categoryTest));
        categoryService.deleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }

//    @Test
//    void softDeleteCategory() {
//        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.ofNullable(categoryTest));
//        when(funkoRepository.save(funkoTest)).thenReturn(funkoTest);
//        Category result = categoryService.softDeleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
//        assertAll(
//                () -> assertEquals(categoryTest.getId(), result.getId()),
//                () -> assertEquals(categoryTest.getCategory(), result.getCategory()),
//                () -> assertEquals(true, result.getSoftDelete()));
//        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
//        verify(categoryRepository, times(1)).save(categoryTest);
//
//    }
}