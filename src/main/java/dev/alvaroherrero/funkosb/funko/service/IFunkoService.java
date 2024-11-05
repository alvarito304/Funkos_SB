package dev.alvaroherrero.funkosb.funko.service;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFunkoService {
    public List<FunkoDTO> getFunkos();
    public List<FunkoDTO> getFunkosByName(String name);
    public FunkoDTO getFunkoById(Long id);
    public FunkoDTO createFunko(Funko funko);
    public FunkoDTO updateFunko(Long id, Funko funko);
    public FunkoDTO deleteFunko(Long id);
    public FunkoDTO updateImage(Long id, MultipartFile image);
}
