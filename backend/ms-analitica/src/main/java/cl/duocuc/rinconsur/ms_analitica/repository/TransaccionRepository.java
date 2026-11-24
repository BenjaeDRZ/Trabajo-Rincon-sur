package cl.duocuc.rinconsur.ms_analitica.repository;

import cl.duocuc.rinconsur.ms_analitica.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByFecha(String fecha);
}