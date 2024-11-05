package dev.alvaroherrero.funkosb.funko.service;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.exceptions.FunkoNotFoundException;
import dev.alvaroherrero.funkosb.funko.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
import dev.alvaroherrero.funkosb.storage.service.IStorageService;
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
    private final FunkoMapper funkoMapper;

    @Autowired
    public FunkoServiceImpl(IFunkoRepository funkoRepository, IStorageService storageService, FunkoMapper funkoMapper) {
        this.funkoMapper = funkoMapper;
        this.funkoRepository = funkoRepository;
        this.storageService = storageService;
    }

    @Override
    public List<FunkoDTO> getFunkos() {
        logger.info("Obteniendo todos los funkos");
        List<Funko> funkos = funkoRepository.findAllActiveFunkos();
        List<FunkoDTO> funkosDTO;
        funkosDTO = funkos.stream().map(funkoMapper::toDTO).toList();
        return funkosDTO;
    }

    @Override
    public List<FunkoDTO> getFunkosByName(String name) {
        logger.info("Obteniendo funkos con nombre " + name);
        List<Funko> funkos = funkoRepository.findAllByName(name);
        List<FunkoDTO> funkosDTO;
        funkosDTO = funkos.stream().map(funkoMapper::toDTO).toList();
        return funkosDTO;
    }

    @Override
    @Cacheable(key = "#id")
    public FunkoDTO getFunkoById(Long id) {
        logger.info("Obteniendo funko con ID " + id);
        return funkoMapper.toDTO(funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        ));
    }



    @Override
    @CachePut(key = "#result.id")
    public FunkoDTO createFunko(Funko funko) {
        logger.info("Creado nuevo funko: " + funko);
        return funkoMapper.toDTO(funkoRepository.save(funko));
    }

    @Override
    @CachePut(key = "#result.id")
    public FunkoDTO updateFunko(Long id, Funko funko) {
        logger.info("Actualizando funko con ID " + id);
        var res = funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        funko.setId(res.getId());
        if (funko.getName() == null) funko.setName(res.getName());
        if (funko.getPrice() == 0) funko.setPrice(res.getPrice());
        if (funko.getCategory() == null) funko.setCategory(res.getCategory());
        funko.setUpdated_at(LocalDateTime.now());
        return funkoMapper.toDTO(funkoRepository.save(funko));
    }

    @Override
    @CacheEvict(key = "#id")
    public FunkoDTO deleteFunko(Long id) {
        logger.info("Eliminando funko con ID " + id);
        var res =  funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        funkoRepository.delete(res);
        return funkoMapper.toDTO(res);
    }

    @Override
    public FunkoDTO updateImage(Long id, MultipartFile image) {
        logger.info("Actualizando imagen de funko con ID " + id);
        var res = funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        var storedImage = storageService.store(image);
        res.setImage(storedImage);
        funkoRepository.save(res);
        return funkoMapper.toDTO(res);
    }
}
