package dev.alvaroherrero.funkosb.funko.service.funkoService;

import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFunkoService {
    public List<Funko> getFunkos();
    public List<Funko> getFunkosByName(String name);
    public Funko getFunkoById(Long id);
    public Funko createFunko(Funko funko);
    public Funko updateFunko(Long id, Funko funko);
    public Funko deleteFunko(Long id);
    public Funko updateImage(Long id, MultipartFile image);
}
