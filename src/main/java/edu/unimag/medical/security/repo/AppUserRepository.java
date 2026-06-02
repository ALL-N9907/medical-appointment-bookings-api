package edu.unimag.medical.security.repo;

import edu.unimag.medical.security.domain.AppUser;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByEmailIgnoreCase(@Email String email);
    boolean existsByEmailIgnoreCase(String email);

}
