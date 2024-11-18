package dev.alvaroherrero.funkosb.auth.repository;

import dev.alvaroherrero.funkosb.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAuthUsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
