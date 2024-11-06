package dev.alvaroherrero.funkosb.category.repository;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ICategoryRepositoryTest {

    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private Category categoryTest;

    @BeforeEach
    void setUp() {
        categoryTest = new Category();
        categoryTest.setCategory(FunkoCategory.SERIE);
        testEntityManager.persist(categoryTest);
    }

    @Test
    void findAllActiveCategories() {
        var result = categoryRepository.findAllActiveCategories();
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(categoryTest.getCategory(), result.getFirst().getCategory())
        );
    }

    @Test
    void findSoftDeletedCategory() {
        var result = categoryRepository.findSoftDeletedCategory(FunkoCategory.SERIE);
        assertAll(
                () -> assertEquals(1, result.getId()),
                () -> assertEquals(categoryTest.getCategory(), result.getCategory())
        );
    }

    @Test
    void findActiveCategory() {
        var result = categoryRepository.findActiveCategory(FunkoCategory.SERIE);
        assertAll(
                () -> assertEquals(1, result.getId()),
                () -> assertEquals(categoryTest.getCategory(), result.getCategory())
        );
    }
}