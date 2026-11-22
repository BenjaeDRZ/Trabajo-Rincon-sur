package cl.duocuc.rinconsur.ms_reportes.repository;

import cl.duocuc.rinconsur.ms_reportes.model.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {
}