package dev.alvaroherrero.funkosb.notifications;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class FunkoNotificationMapperTest {

    private FunkoNotificationMapper funkoNotificationMapper = new FunkoNotificationMapper();

    Category category = Category.builder()
            .category(FunkoCategory.SERIE)
            .build();

    Funko funkoTest = Funko.builder()
            .id(0L)
            .price(10.0f)
            .name("Test Funko")
            .category(category)
            .build();

    @Test
    void toDTO() {
        var res = funkoNotificationMapper.toDTO(funkoTest);
        assertNotNull(res);
        assertEquals(funkoTest.getName(), res.getNombre());
    }
}