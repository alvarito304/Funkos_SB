package dev.alvaroherrero.funkosb.category.service;

import dev.alvaroherrero.funkosb.category.exceptions.CategoryNotFoundException;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.repository.ICategoryRepository;
import dev.alvaroherrero.funkosb.funko.exceptions.FunkoNotFoundException;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
import dev.alvaroherrero.funkosb.funko.service.FunkoServiceImpl;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
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
    private ICategoryRepository categoryRepository;

    @Mock
    private IFunkoService funkoService;

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
        funkoTest.setId(1L);
        funkoTest.setName("Test Funko");
        funkoTest.setPrice(10.0F);
        funkoTest.setCategory(categoryTest);

        categoryTest.setFunkos(List.of(funkoTest));
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
    void findCategoryByIdNotFound() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.empty());
        var result = assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f")));
        assertEquals("Category with id 1aa1b369-beed-4eef-b63c-9f6330c71b0f not found", result.getMessage());
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }

    @Test
    void createCategory_New() {
        // Configurar el mock para devolver null (categoría no existente)
        when(categoryRepository.findSoftDeletedCategory(FunkoCategory.SERIE)).thenReturn(null);
        when(categoryRepository.findActiveCategory(FunkoCategory.SERIE)).thenReturn(null);
        when(categoryRepository.save(categoryTest)).thenReturn(categoryTest);

        // Llamar al método
        Category result = categoryService.createCategory(categoryTest);

        // Verificar que la categoría se guarda como nueva
        assertEquals(categoryTest, result);
        verify(categoryRepository, times(1)).save(categoryTest);
        verify(funkoService, times(0)).updateFunko(any(), any());
    }

    @Test
    void testCreateCategory_YaExiste() {
        // Configurar el mock para devolver la categoría ya activa
        categoryTest.setSoftDelete(false);
        when(categoryRepository.findActiveCategory(FunkoCategory.SERIE)).thenReturn(categoryTest);

        // Llamar al método
        Category result = categoryService.createCategory(categoryTest);

        // Verificar que la categoría se devuelve sin cambios
        assertEquals(categoryTest, result);
        verify(categoryRepository, times(0)).save(any());
        verify(funkoService, times(0)).updateFunko(any(), any());
    }

    @Test
    void testCreateCategory_ReactivarCategoria() {
        // Configurar el mock para que devuelva la categoría soft-deleted
        when(categoryRepository.findSoftDeletedCategory(FunkoCategory.SERIE)).thenReturn(categoryTest);
        when(categoryRepository.save(categoryTest)).thenReturn(categoryTest);

        // Llamar al método
        Category result = categoryService.createCategory(categoryTest);

        // Verificar los cambios esperados
        assertFalse(result.getSoftDelete());
        assertNotNull(result.getUpdatedAt());
        verify(funkoService, times(1)).updateFunko(funkoTest.getId(), funkoTest);
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
    void updateCategoryNotFound() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.empty());
        var result = assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"), categoryTest));
        assertEquals("Category with id 1aa1b369-beed-4eef-b63c-9f6330c71b0f not found", result.getMessage());
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        }


    @Test
    void deleteCategory() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.ofNullable(categoryTest));
        categoryService.deleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }

    @Test
    void deleteCategoryNotFound() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.empty());
        var result = assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f")));
        assertEquals("Category with id 1aa1b369-beed-4eef-b63c-9f6330c71b0f not found", result.getMessage());
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }

    @Test
    void softDeleteCategory() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.of(categoryTest));
        when(categoryRepository.save(categoryTest)).thenReturn(categoryTest);

        Category result = categoryService.softDeleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));

        assertAll(
                () -> assertEquals(categoryTest.getId(), result.getId()),
                () -> assertEquals(categoryTest.getCategory(), result.getCategory()),
                () -> assertTrue(result.getSoftDelete())
        );

        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        verify(categoryRepository, times(1)).save(categoryTest);
        verify(funkoService, times(1)).updateFunko(any(), any()); // Verifica que el método updateFunko se llame
    }

    @Test
    void softDeleteCategoryNotFound() {
        when(categoryRepository.findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(Optional.empty());
        var result = assertThrows(CategoryNotFoundException.class, () -> categoryService.softDeleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f")));
        assertEquals("Category with id 1aa1b369-beed-4eef-b63c-9f6330c71b0f not found", result.getMessage());
        verify(categoryRepository, times(1)).findById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }
}