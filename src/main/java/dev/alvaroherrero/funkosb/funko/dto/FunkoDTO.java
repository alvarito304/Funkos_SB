package dev.alvaroherrero.funkosb.funko.dto;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.global.validations.validanotations.ValidCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FunkoDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String name;
    @Min(value = 0, message = "El precio debe ser un mayor que 0")
    private float price;

    private Category category;

    public FunkoDTO(String name, Category category, float price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public FunkoDTO() {
    }
}