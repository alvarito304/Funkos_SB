package dev.alvaroherrero.funkosb.funko.mapper;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.stereotype.Component;

@Component
public class FunkoMapper {
    public FunkoDTO toDTO(Funko funko) {
        FunkoDTO dto = new FunkoDTO();
        dto.setName(funko.getName());
        dto.setPrice(funko.getPrice());
        dto.setCategory(funko.getCategory());
        return dto;
    }

    public Funko toEntity(FunkoDTO dto) {
        return new Funko( dto.getName(), dto.getPrice(), dto.getCategory());
    }
}
