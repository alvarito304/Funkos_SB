package dev.alvaroherrero.funkosb.services;

import dev.alvaroherrero.funkosb.model.Funko;

import java.util.List;

public interface IFunkoService {
    public List<Funko> getFunkos();
    public Funko getFunkoById(Long id);
    public Funko createFunko(Funko funko);
    public Funko updateFunko(Long id, Funko funko);
    public Funko deleteFunko(Long id);
}
