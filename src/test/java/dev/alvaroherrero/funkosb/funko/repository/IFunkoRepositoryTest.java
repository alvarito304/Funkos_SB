package dev.alvaroherrero.funkosb.funko.repository;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.model.Funko;

import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class IFunkoRepositoryTest {

    @Autowired
    private IFunkoRepository funkoRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private Funko funkoTest;
    private Category categoryTest;
    @BeforeEach
    void setUp() {
        categoryTest = new Category();
        categoryTest.setCategory(FunkoCategory.SERIE);
        testEntityManager.persist(categoryTest);

        funkoTest = new Funko();
        funkoTest.setName("Test Funko");
        funkoTest.setPrice(10.0F);
        funkoTest.setCategory(categoryTest);
        testEntityManager.persist(funkoTest);
    }
    @Test
    void findAllByName() {
        // arrange
        // act
        var result = funkoRepository.findAllByName("Test Funko");

        // assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(funkoTest.getName(), result.getFirst().getName())
        );
    }

//    @Test
//    void finAllActiveFunkos() {
//        // arrange
//        // act
//        var result = funkoRepository.findAllActiveFunkos();
//
//        // assert
//        assertAll(
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals(funkoTest.getName(), result.getFirst().getName())
//        );
//    }

    @Test
    void finAllSoftDeletedFunkos() {
        // arrange
        funkoTest.setFunkoSoftDeleted(true);
        testEntityManager.persist(funkoTest);
        // act
        var result = funkoRepository.findAllSoftDeletedFunkos();

        // assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(funkoTest.getName(), result.getFirst().getName())
        );
    }
}