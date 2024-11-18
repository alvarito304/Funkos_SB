package dev.alvaroherrero.funkosb.funko.mapper;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.repository.ICategoryRepository;
import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FunkoMapperTest {
    @Mock
    ICategoryRepository categoryRepository;

    @InjectMocks
    FunkoMapper mapper;

    Category category = Category.builder()
            .category(FunkoCategory.SERIE)
            .build();

    Funko funkoTest = Funko.builder()
            .id(0L)
            .price(10.0f)
            .name("Test Funko")
            .category(category)
            .build();

    FunkoDTO funkoDtoTest = new FunkoDTO(funkoTest.getName(), funkoTest.getCategory().getCategory(), funkoTest.getPrice(),funkoTest.getCategory().getId(), funkoTest.getImage(), funkoTest.getStock());


    @Test
    void toDTO() {
        FunkoDTO actual = mapper.toDTO(funkoTest);
        assertEquals(funkoDtoTest, actual);
    }

    @Test
    void toEntity() {
        when(categoryRepository.findById(funkoTest.getCategory().getId())).thenReturn(Optional.of(category));
        Funko actual = mapper.toEntity(funkoDtoTest);
        assertEquals("Test Funko", actual.getName());
        assertEquals(FunkoCategory.SERIE, actual.getCategory().getCategory());
    }
}
