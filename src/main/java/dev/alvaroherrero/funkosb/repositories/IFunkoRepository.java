package dev.alvaroherrero.funkosb.repositories;

import dev.alvaroherrero.funkosb.model.Funko;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface IFunkoRepository {
    public List<Funko> getFunkos();
    public Optional<Funko> getFunkoById(Long id);
    public Funko createFunko(Funko funko);
    public Optional<Funko> updateFunko(Long id, Funko funko);
    public Optional<Funko> deleteFunko(Long id);
}
