package dev.alvaroherrero.funkosb.funko.dto;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.global.validations.validanotations.ValidCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
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

    private String imagen;

    private Integer stock;

    public FunkoDTO(String name, FunkoCategory category, float price, UUID categoryId, String imagen, Integer stock) {
        this.name = name;
        this.category = category;
        this.categoryId = categoryId;
        this.price = price;
        this.imagen = imagen;
        this.stock = stock;
    }

    public FunkoDTO() {
    }
}