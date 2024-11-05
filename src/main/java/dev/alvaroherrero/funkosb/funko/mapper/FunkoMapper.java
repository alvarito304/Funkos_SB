package dev.alvaroherrero.funkosb.funko.mapper;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
public class FunkoMapper {
    public static FunkoDTO toDTO(Funko funko) {
        FunkoDTO dto = new FunkoDTO();
        dto.setName(funko.getName());
        dto.setPrice(funko.getPrice());
        dto.setCategory(funko.getCategory());
        return dto;
    }
    public static Funko toEntity(FunkoDTO dto) {
        return new Funko(dto.getName(), dto.getPrice(), dto.getCategory());
    }
}
