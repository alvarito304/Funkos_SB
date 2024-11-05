//package dev.alvaroherrero.funkosb.repositories;
//
//import dev.alvaroherrero.funkosb.funko.model.Funko;
//import dev.alvaroherrero.funkosb.models.funkocategory.FunkoCategory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import static org.junit.jupiter.api.Assertions.*;
//@DataJpaTest
//class IFunkoRepositoryTest {
//
//    @Autowired
//    private IFunkoRepository funkoRepository;
//    @Autowired
//    private TestEntityManager testEntityManager;
//
//    private Funko funkoTest;
//    @BeforeEach
//    void setUp() {
//        funkoTest = new Funko();
//        funkoTest.setName("Test Funko");
//        funkoTest.setPrice(10.0F);
//        funkoTest.setCategory(FunkoCategory.DISNEY);
//        testEntityManager.persist(funkoTest);
//    }
//    @Test
//    void findAllByName() {
//        // arrange
//        // act
//        var result = funkoRepository.findAllByName("Test Funko");
//
//        // assert
//        assertAll(
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals(funkoTest.getName(), result.getFirst().getName())
//        );
//    }
//}