package dev.alvaroherrero.funkosb.services.funkoService;

import dev.alvaroherrero.funkosb.models.Funko;

import java.util.List;

public interface IFunkoService {
    public List<Funko> getFunkos();
    public List<Funko> getFunkosByName(String name);
    public Funko getFunkoById(Long id);
    public Funko createFunko(Funko funko);
    public Funko updateFunko(Long id, Funko funko);
    public Funko deleteFunko(Long id);
}
