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
        // Limpiar la base de datos ficticia antes de cada prueba
        testEntityManager.getEntityManager().createQuery("DELETE FROM Funko ").executeUpdate();

        testEntityManager.getEntityManager().createQuery("DELETE FROM Category").executeUpdate();

        testEntityManager.flush();

        // Insertar datos de prueba
        categoryTest = new Category();
        categoryTest.setCategory(FunkoCategory.SERIE);
        testEntityManager.persistAndFlush(categoryTest);
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
        categoryTest.setSoftDelete(true);
        var result = categoryRepository.findSoftDeletedCategory(FunkoCategory.SERIE);
        assertAll(
                () -> assertEquals(categoryTest.getCategory(), result.getCategory())
        );
    }

    @Test
    void findActiveCategory() {
        var result = categoryRepository.findActiveCategory(FunkoCategory.SERIE);
        assertAll(
                () -> assertEquals(categoryTest.getCategory(), result.getCategory())
        );
    }
}