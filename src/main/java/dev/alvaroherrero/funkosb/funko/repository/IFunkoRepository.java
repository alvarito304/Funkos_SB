package dev.alvaroherrero.funkosb.funko.repository;

import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFunkoRepository extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {
    List<Funko> findAllByName (String name);

    @Query("SELECT f From Funko f where f.funkoSoftDeleted = true")
    List<Funko> findAllSoftDeletedFunkos();
}
