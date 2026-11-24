package cl.duocuc.rinconsur.ms_analitica.repository;

import cl.duocuc.rinconsur.ms_analitica.model.Conteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConteoRepository extends JpaRepository<Conteo, Long> {
    List<Conteo> findByFecha(String fecha);
}
