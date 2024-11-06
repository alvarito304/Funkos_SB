package dev.alvaroherrero.funkosb.category.dto;

import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Builder
public class CategoryDTO {
    private FunkoCategory category;

    public CategoryDTO(FunkoCategory category) {
        this.category = category;
    }
    public CategoryDTO() {

    }
}
