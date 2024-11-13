package dev.alvaroherrero.funkosb.funko.mapper;

import dev.alvaroherrero.funkosb.category.exceptions.CategoryNotFoundException;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.repository.ICategoryRepository;
import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.stereotype.Component;

@Component
public class FunkoMapper {

    private final ICategoryRepository categoryRepository;

    public FunkoMapper(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public FunkoDTO toDTO(Funko funko) {
        FunkoDTO dto = new FunkoDTO();
        dto.setName(funko.getName());
        dto.setPrice(funko.getPrice());
        dto.setCategory(funko.getCategory().getCategory());
        dto.setCategoryId(funko.getCategory().getId());
        dto.setImagen(funko.getImage());
        dto.setStock(funko.getStock());
        return dto;
    }

    public Funko toEntity(FunkoDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));
        return new Funko( dto.getName(), dto.getPrice(), category, dto.getImagen(), dto.getStock());
    }
}
