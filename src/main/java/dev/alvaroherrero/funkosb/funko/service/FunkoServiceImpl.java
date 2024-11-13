package dev.alvaroherrero.funkosb.funko.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.exceptions.FunkoNotFoundException;
import dev.alvaroherrero.funkosb.funko.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.repository.IFunkoRepository;
import dev.alvaroherrero.funkosb.funko.storage.service.IStorageService;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.global.websockets.WebSocketConfig;
import dev.alvaroherrero.funkosb.global.websockets.WebSocketHandler;
import dev.alvaroherrero.funkosb.notifications.FunkoNotificationDTO;
import dev.alvaroherrero.funkosb.notifications.FunkoNotificationMapper;
import dev.alvaroherrero.funkosb.notifications.Notificacion;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"Funko"})
public class FunkoServiceImpl implements  IFunkoService {
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final IFunkoRepository funkoRepository;
    private final IStorageService storageService;
    private final FunkoMapper funkoMapper;

    private WebSocketHandler webSocketHandler;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final FunkoNotificationMapper funkoNotificacionMapper;

    @Autowired
    public FunkoServiceImpl(IFunkoRepository funkoRepository, IStorageService storageService, WebSocketHandler webSocketHandler, WebSocketConfig webSocketConfig, ObjectMapper objectMapper, FunkoNotificationMapper funkoNotificacionMapper, FunkoMapper funkoMapper) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketConfig = webSocketConfig;
        this.objectMapper = objectMapper;
        this.funkoNotificacionMapper = funkoNotificacionMapper;
        this.funkoRepository = funkoRepository;
        this.storageService = storageService;
        this.funkoMapper = funkoMapper;
    }

    @Override
    public Page<Funko> getFunkos(Optional<String> name, Optional<FunkoCategory> category, Optional<Boolean> softDeleted, Optional<Double> price, Pageable pageable) {
        logger.info("Obteniendo todos los funkos");
        Specification<Funko> specNameFunko = (root, query, criteriaBuilder) ->
                name.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(criteriaBuilder::conjunction);

        Specification<Funko> specCategoryFunko = (root, query, criteriaBuilder) ->
                category.map(c -> {
                    Join<Funko, Category> categoriaJoin = root.join("category");
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("name")), "%" + c.name().toLowerCase() + "%");
                }).orElseGet(criteriaBuilder::conjunction);

        Specification<Funko> specSoftDeletedFunko = (root, query, criteriaBuilder) ->
                softDeleted.map(sd -> criteriaBuilder.equal(root.get("funkoSoftDeleted"), sd))
                        .orElse(criteriaBuilder.isFalse(root.get("funkoSoftDeleted")));


        Specification<Funko> specPriceFunko = (root, query, criteriaBuilder) ->
                price.map(p -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), p))
                        .orElseGet(criteriaBuilder::conjunction);

        Specification<Funko> criterio = Specification.where(specNameFunko)
                .and(specCategoryFunko)
                .and(specSoftDeletedFunko)
                .and(specPriceFunko);

        Page<Funko> funkos = funkoRepository.findAll(criterio, pageable);
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
        onChange(Notificacion.Tipo.CREATE, funko);
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
        if (funko.getImage() == null) funko.setImage(res.getImage());
        if (funko.getStock() == null) funko.setStock(res.getStock());
        funko.setUpdated_at(LocalDateTime.now());
        onChange(Notificacion.Tipo.UPDATE, funko);
        var funkoDTO = funkoRepository.save(funko);
        return funkoDTO;
    }

    @Override
    @CacheEvict(key = "#id")
    public Funko deleteFunko(Long id) {
        logger.info("Eliminando funko con ID " + id);
        var res =  funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFoundException(id)
        );
        funkoRepository.delete(res);
        onChange(Notificacion.Tipo.DELETE, res);
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

    void onChange(Notificacion.Tipo tipo, Funko data) {
        log.debug("Servicio de productos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketHandler == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketHandler = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notificacion<FunkoNotificationDTO> notificacion = new Notificacion<>(
                    "PRODUCTOS",
                    tipo,
                    funkoNotificacionMapper.toDTO(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketHandler.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}


