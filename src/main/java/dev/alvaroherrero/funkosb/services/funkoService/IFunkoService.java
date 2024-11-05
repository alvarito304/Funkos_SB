package dev.alvaroherrero.funkosb.services.funkoService;

import dev.alvaroherrero.funkosb.models.Category;
import dev.alvaroherrero.funkosb.models.Funko;
import dev.alvaroherrero.funkosb.models.funkocategory.FunkoCategory;

import java.util.List;
import java.util.UUID;

public interface IFunkoService {
    public List<Funko> getFunkos();
    public List<Funko> getFunkosByName(String name);
    public Funko getFunkoById(Long id);
    public Funko createFunko(Funko funko);
    public Funko updateFunko(Long id, Funko funko);
    public Funko deleteFunko(Long id);
}
