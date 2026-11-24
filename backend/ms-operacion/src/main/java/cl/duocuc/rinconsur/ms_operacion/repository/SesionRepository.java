package cl.duocuc.rinconsur.ms_operacion.repository;

import cl.duocuc.rinconsur.ms_operacion.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, String> {
}