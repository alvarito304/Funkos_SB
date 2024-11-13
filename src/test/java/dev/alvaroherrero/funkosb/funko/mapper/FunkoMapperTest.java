/*
package dev.alvaroherrero.funkosb.funko.mapper;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {

    FunkoMapper mapper = new FunkoMapper();

    Category category = Category.builder()
            .category(FunkoCategory.SERIE)
            .build();

    Funko funkoTest = Funko.builder()
            .id(0L)
            .price(10.0f)
            .name("Test Funko")
            .category(category)
            .build();

    FunkoDTO funkoDtoTest = new FunkoDTO(funkoTest.getName(), funkoTest.getCategory(), funkoTest.getPrice());


    @Test
    void toDTO() {
        FunkoDTO actual = mapper.toDTO(funkoTest);
        assertEquals(funkoDtoTest, actual);
    }

    @Test
    void toEntity() {
        Funko actual = mapper.toEntity(funkoDtoTest);
        assertEquals("Test Funko", actual.getName());
        assertEquals(FunkoCategory.SERIE, actual.getCategory().getCategory());
    }
}*/
