package dev.alvaroherrero.funkosb.services.funkoService;

import dev.alvaroherrero.funkosb.exceptions.FunkoNotFoundException;
import dev.alvaroherrero.funkosb.models.Funko;
import dev.alvaroherrero.funkosb.repositories.IFunkoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@CacheConfig(cacheNames = {"Funko"})
public class FunkoServiceImpl implements  IFunkoService {
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private IFunkoRepository funkoRepository;

    @Autowired
    public FunkoServiceImpl(IFunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
    }

    @Override
    public List<Funko> getFunkos() {
        logger.info("Obteniendo todos los funkos");
        return funkoRepository.findAll();
    }

    @Override
    public List<Funko> getFunkosByName(String name) {
        logger.info("Obteniendo funkos con nombre " + name);
        return funkoRepository.findAllByName(name);
    }

    @Override
    @Cacheable(key = "#id")
    public Funko getFunkoById(Long id) {
        logger.info("Obteniendo funko con ID " + id);
        return funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko createFunko(Funko funko) {
        logger.info("Creado nuevo funko: " + funko);
        return funkoRepository.save(funko);
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko updateFunko(Long id, Funko funko) {
        logger.info("Actualizando funko con ID " + id);
        var res = funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        funko.setId(res.getId());
        if (funko.getName() == null) funko.setName(res.getName());
        if (funko.getPrice() == 0) funko.setPrice(res.getPrice());
        if (funko.getCategory() == null) funko.setCategory(res.getCategory());
        funko.setUpdated_at(LocalDateTime.now());
        return funkoRepository.save(funko);
    }

    @Override
    @CacheEvict(key = "#id")
    public Funko deleteFunko(Long id) {
        logger.info("Eliminando funko con ID " + id);
        var res =  funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        funkoRepository.delete(res);
        return res;
    }
}
