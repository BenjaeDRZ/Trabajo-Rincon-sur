package cl.duocuc.rinconsur.ms_operacion.repository;

import cl.duocuc.rinconsur.ms_operacion.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    List<Registro> findBySesionAndAnuladoFalse(String sesion);
    Optional<Registro> findBySesionAndNumero(String sesion, String numero);
}
