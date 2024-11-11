package dev.alvaroherrero.funkosb.funko.service;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFunkoService {
    public Page<Funko> getFunkos(Pageable pageable);
    public List<Funko> getFunkosByName(String name);
    public Funko getFunkoById(Long id);
    public Funko createFunko(Funko funko);
    public Funko updateFunko(Long id, Funko funko);
    public Funko deleteFunko(Long id);
    public Funko updateImage(Long id, MultipartFile image);
}
