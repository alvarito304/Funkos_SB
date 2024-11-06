package dev.alvaroherrero.funkosb.funko.service;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.exceptions.FunkoNotFoundException;
import dev.alvaroherrero.funkosb.funko.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
import dev.alvaroherrero.funkosb.funko.storage.service.IStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
@Service
@CacheConfig(cacheNames = {"Funko"})
public class FunkoServiceImpl implements  IFunkoService {
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final IFunkoRepository funkoRepository;
    private final IStorageService storageService;

    @Autowired
    public FunkoServiceImpl(IFunkoRepository funkoRepository, IStorageService storageService) {
        this.funkoRepository = funkoRepository;
        this.storageService = storageService;
    }

    @Override
    public List<Funko> getFunkos() {
        logger.info("Obteniendo todos los funkos");
        List<Funko> funkos = funkoRepository.findAllActiveFunkos();
        return funkos;
    }

    @Override
    public List<Funko> getFunkosByName(String name) {
        logger.info("Obteniendo funkos con nombre " + name);
        List<Funko> funkos = funkoRepository.findAllByName(name);
        return funkos;
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

        // Guardar la entidad directamente
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

    @Override
    public Funko updateImage(Long id, MultipartFile image) {
        logger.info("Actualizando imagen de funko con ID " + id);
        var res = funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        var storedImage = storageService.store(image);
        res.setImage(storedImage);
        funkoRepository.save(res);
        return res;
    }
}
