package dev.alvaroherrero.funkosb.repositories;

import dev.alvaroherrero.funkosb.model.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface IFunkoRepository extends JpaRepository<Funko, Long> {
    List<Funko> findAllByName (String name);
}
