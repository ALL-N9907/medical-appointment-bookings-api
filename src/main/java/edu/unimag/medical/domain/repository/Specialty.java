package edu.unimag.medical.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Specialty extends JpaRepository<Specialty, UUID> {
}
