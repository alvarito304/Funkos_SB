//package dev.alvaroherrero.funkosb.services;
//
//import dev.alvaroherrero.funkosb.funko.exceptions.FunkoNotFoundException;
//import dev.alvaroherrero.funkosb.funko.model.Funko;
//import dev.alvaroherrero.funkosb.models.funkocategory.FunkoCategory;
//import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
//import dev.alvaroherrero.funkosb.services.funkoService.FunkoServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class FunkoServiceImplTest {
//
//    @Mock
//    private IFunkoRepository funkoRepository;
//
//    @InjectMocks
//    private FunkoServiceImpl funkoService;
//
//    private Funko funkoTest;
//
//    @BeforeEach
//    void setUp() {
//        funkoTest = new Funko();
//        funkoTest.setName("Test Funko");
//        funkoTest.setPrice(10.0F);
//        funkoTest.setCategory(FunkoCategory.ACTION);
//    }
//
//    @Test
//    void getFunkos() {
//        //comportamiento del mock
//        when(funkoRepository.findAll()).thenReturn(List.of(funkoTest));
//        // act
//        var result = funkoService.getFunkos();
//        // assert
//
//        assertAll(
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals(funkoTest.getName(), result.getFirst().getName())
//        );
//
//        //verify
//        verify(funkoRepository, times(1)).findAll();
//    }
//
//    @Test
//    void getFunkosByName() {
//        //comportamiento del mock
//        when(funkoRepository.findAllByName(funkoTest.getName())).thenReturn(List.of(funkoTest));
//        // act
//        var result = funkoService.getFunkosByName(funkoTest.getName());
//        // assert
//        assertAll(
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals(funkoTest.getName(), result.getFirst().getName())
//        );
//
//        verify(funkoRepository, times(1)).findAllByName(funkoTest.getName());
//    }
//
//    @Test
//    void getFunkoById() {
//        //comportamiento del mock
//        when(funkoRepository.findById(0L)).thenReturn(Optional.ofNullable(funkoTest));
//        //act
//        var result = funkoService.getFunkoById(0L);
//        // assert
//        assertAll(
//                () -> assertEquals(0L, result.getId()),
//                () -> assertEquals(funkoTest.getName(), result.getName()),
//                () -> assertEquals(funkoTest.getCategory(), result.getCategory())
//        );
//        // verify
//        verify(funkoRepository, times(1)).findById(0L);
//    }
//
//    @Test
//    void getFunkoByIdNotFound() {
//        //comportamiento del mock
//        when(funkoRepository.findById(funkoTest.getId())).thenReturn(Optional.empty());
//        //act
//        var result = assertThrows(FunkoNotFoundException.class, () -> funkoService.getFunkoById(0L));
//        //assert
//        assertEquals("Funko con id " + funkoTest.getId()+  " not found", result.getMessage());
//        // verify
//        verify(funkoRepository, times(1)).findById(0L);
//    }
//    @Test
//    void createFunko() {
//        //comportamiento del mock
//        when(funkoRepository.save(funkoTest)).thenReturn(funkoTest);
//        //act
//        var result = funkoService.createFunko(funkoTest);
//        // assert
//        assertAll(
//                () -> assertEquals(funkoTest.getId(), result.getId()),
//                () -> assertEquals(funkoTest.getName(), result.getName()),
//                () -> assertEquals(funkoTest.getCategory(), result.getCategory())
//        );
//        // verify
//        verify(funkoRepository, times(1)).save(funkoTest);
//    }
//
//    @Test
//    void updateFunko() {
//        //comportamiento del mock
//        when(funkoRepository.findById(0L)).thenReturn(Optional.ofNullable(funkoTest));
//        when(funkoRepository.save(funkoTest)).thenReturn(funkoTest);
//        //act
//        var result = funkoService.updateFunko(0L, funkoTest);
//        // assert
//        assertAll(
//                () -> assertEquals(funkoTest.getId(), result.getId()),
//                () -> assertEquals(funkoTest.getName(), result.getName()),
//                () -> assertEquals(funkoTest.getCategory(), result.getCategory())
//        );
//        // verify
//        verify(funkoRepository, times(1)).findById(0L);
//        verify(funkoRepository, times(1)).save(funkoTest);
//    }
//
//    @Test
//    void updateFunkoNotFound() {
//        //comportamiento del mock
//        when(funkoRepository.findById(funkoTest.getId())).thenReturn(Optional.empty());
//        //act
//        var result = assertThrows(FunkoNotFoundException.class, () -> funkoService.updateFunko(funkoTest.getId(), funkoTest));
//        //assert
//        assertEquals("Funko con id " + funkoTest.getId() +  " not found", result.getMessage());
//        // verify
//        verify(funkoRepository, times(1)).findById(funkoTest.getId());
//    }
//
//    @Test
//    void deleteFunko() {
//        //comportamiento del mock
//        when(funkoRepository.findById(0L)).thenReturn(Optional.ofNullable(funkoTest));
//        //act
//        funkoService.deleteFunko(0L);
//        // assert
//        verify(funkoRepository, times(1)).delete(funkoTest);
//    }
//
//    @Test
//    void deleteFunkoNotFound() {
//        //comportamiento del mock
//        when(funkoRepository.findById(funkoTest.getId())).thenReturn(Optional.empty());
//        //act
//        var result = assertThrows(FunkoNotFoundException.class, () -> funkoService.deleteFunko(funkoTest.getId()));
//        //assert
//        assertEquals("Funko con id " + funkoTest.getId() +  " not found", result.getMessage());
//        // verify
//        verify(funkoRepository, times(1)).findById(funkoTest.getId());
//    }
//}