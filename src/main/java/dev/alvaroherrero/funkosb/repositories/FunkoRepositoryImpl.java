package dev.alvaroherrero.funkosb.repositories;

import dev.alvaroherrero.funkosb.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class FunkoRepositoryImpl implements IFunkoRepository {
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);
    private final Map<Long, Funko> funkoMap = new ConcurrentHashMap<>();

    @Override
    public List<Funko> getFunkos() {
        logger.info("Funkos");
        return funkoMap.values().stream().toList();
    }

    @Override
    public Optional<Funko> getFunkoById(Long id) {
        logger.info("Funko con ID " + id);
        return Optional.ofNullable(funkoMap.get(id));
    }

    @Override
    public Funko createFunko(Funko funko) {
        funkoMap.put(funko.getId(), funko);
        logger.info("Funko añadido: " + funko);
        return funko;
    }

    @Override
    public Optional<Funko> updateFunko(Long id, Funko funko) {
        Funko existingFunko = funkoMap.get(id);
        if (existingFunko != null) {
            existingFunko.setPrice(funko.getPrice()); // Solo actualiza el precio
            // Actualiza la fecha de modificación
            existingFunko.setUpdated_at(LocalDateTime.now());
            // Vuelve a poner el Funko actualizado en el Map
            funkoMap.put(id, existingFunko);
            logger.info("Funko actualizado: " + existingFunko);
            return Optional.of(existingFunko);
        } else {
            logger.warn("Funko con ID " + id + " no encontrado.");
            return Optional.empty();
        }
    }


    @Override
    public Optional<Funko> deleteFunko(Long id) {
        logger.info("Funko eliminado con ID " + id);
        return Optional.ofNullable(funkoMap.remove(id));
    }
}
