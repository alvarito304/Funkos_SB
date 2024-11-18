package dev.alvaroherrero.funkosb.category.service;

import dev.alvaroherrero.funkosb.category.exceptions.CategoryNotFoundException;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.repository.ICategoryRepository;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@CacheConfig(cacheNames = {"Category"})
public class CategoryServiceImpl implements ICategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private ICategoryRepository categoryRepository;
    private IFunkoService funkoService;

    @Autowired
    public CategoryServiceImpl(ICategoryRepository categoryRepository, IFunkoService funkoService) {
        this.categoryRepository = categoryRepository;
        this.funkoService = funkoService;
    }

    @Override
    public List<Category> getAllCategories() {
        logger.info("Obteniendo todas las categorías");
        return categoryRepository.findAllActiveCategories();
    }

    @Override
    @Cacheable(key = "#id")
    public Category getCategoryById(UUID id) {
        logger.info("Obteniendo categoría con ID " + id);
        return categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException(id)
        );
    }

    @Override
    @CachePut(key = "#result.id")
    public Category createCategory(Category category) {
        logger.info("Creando nueva categoría");
        // revisar si la categoría ya existe y si es asi la actualizamos para que vuelva a ser activa
        var softDeletedCategory = categoryRepository.findSoftDeletedCategory(category.getCategory());
        if ( softDeletedCategory != null) {
            logger.info("La categoría {} ya existe, se actualizó su estado a activo.", category.getCategory());
            softDeletedCategory.setSoftDelete(false);
            softDeletedCategory.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(softDeletedCategory);
            // hacemos q sus funkos sean visibles
            for (var funko : softDeletedCategory.getFunkos()) {
                funko.setFunkoSoftDeleted(false);
                funkoService.updateFunko(funko.getId(), funko);
            }
            return softDeletedCategory;
        }
        softDeletedCategory = categoryRepository.findActiveCategory(category.getCategory());
        if (softDeletedCategory != null) {
            logger.info("La categoría ya existe y su id es {}", category.getId());
            return softDeletedCategory;
        }
        return categoryRepository.save(category);
    }

    @Override
    @CachePut(key = "#result.id")
    public Category updateCategory(UUID id, Category category) {
        logger.info("Actualizando categoría con ID " + id);
        var res = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException(id)
        );
        res.setCategory(category.getCategory());
        res.setCreatedAt(category.getCreatedAt());
        res.setUpdatedAt(LocalDateTime.now());

        // Reemplaza la colección inmutable por una mutable antes de modificar
        if (res.getFunkos() != null) {
            res.setFunkos(new ArrayList<>(res.getFunkos())); // Crear una copia mutable
            res.getFunkos().clear();
            if (category.getFunkos() != null) {
                res.getFunkos().addAll(category.getFunkos());
            }
        } else if (category.getFunkos() != null) {
            res.setFunkos(new ArrayList<>(category.getFunkos()));
        }

        return categoryRepository.save(res);
    }



    @Override
    @CacheEvict(key = "#id")
    public Category deleteCategory(UUID id) {
        logger.info("Eliminando categoría con ID " + id);
        var res = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException(id)
        );
        categoryRepository.delete(res);
        return res;
    }

    @Override
    @CacheEvict(key = "#id")
    public Category softDeleteCategory(UUID id) {
        logger.info("Soft eliminando categoría con ID " + id);
        // Buscamos la categoria
        var res = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException(id)
        );
        // Marcamos la categoria como borrada
        res.setSoftDelete(true);
        // Guardamos la categoria en la base de datos
        categoryRepository.save(res);
        // Buscamos todos los funkos de la categoria y les marcamos como borrados
        for (var funko: res.getFunkos()) {
            funko.setFunkoSoftDeleted(true);
            funkoService.updateFunko(funko.getId(), funko);
        }
        return res;
    }
}
