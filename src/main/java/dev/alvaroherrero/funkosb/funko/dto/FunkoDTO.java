package dev.alvaroherrero.funkosb.funko.dto;

import dev.alvaroherrero.funkosb.category.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FunkoDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @Min(value = 0, message = "El precio debe ser un mayor que 0")
    private float price;

    private Category category;
}