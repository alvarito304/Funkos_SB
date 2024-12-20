package dev.alvaroherrero.funkosb.category.repository;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ICategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.softDelete = false")
    List<Category> findAllActiveCategories();

//    @Modifying
//    @Query("UPDATE Category c SET c.softDelete = true where c.id = :id")
//    void softDelete(UUID id);
    @Query("SELECT c FROM Category c WHERE c.category = :category and c.softDelete = true")
    Category findSoftDeletedCategory(FunkoCategory category);

    @Query("SELECT c FROM Category c WHERE c.category = :category and c.softDelete = false")
    Category findActiveCategory(FunkoCategory category);
}
