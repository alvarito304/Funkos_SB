package dev.alvaroherrero.funkosb.funko.dto;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.global.validations.validanotations.ValidCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class FunkoDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String name;
    @Min(value = 0, message = "El precio debe ser un mayor que 0")
    private float price;

    private FunkoCategory category;
    private UUID categoryId;

    public FunkoDTO(String name, FunkoCategory category, float price, UUID categoryId) {
        this.name = name;
        this.category = category;
        this.categoryId = categoryId;
        this.price = price;
    }

    public FunkoDTO() {
    }
}