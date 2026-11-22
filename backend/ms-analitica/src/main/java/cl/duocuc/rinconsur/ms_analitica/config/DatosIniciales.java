package cl.duocuc.rinconsur.ms_analitica.config;

import cl.duocuc.rinconsur.ms_analitica.model.Conteo;
import cl.duocuc.rinconsur.ms_analitica.repository.ConteoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Mismos conteos de ejemplo que trae la app (2026-09-15), para que haya datos al probar. */
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarConteos(ConteoRepository repository) {
        return args -> {
            if (repository.count() > 0) return;
            repository.saveAll(List.of(
                    conteo("2026-09-15", "A1", "DIA", 2040),
                    conteo("2026-09-15", "A2", "DIA", 1105),
                    conteo("2026-09-15", "A3", "DIA", 730),
                    conteo("2026-09-15", "A4", "DIA", 1860)
            ));
        };
    }

    private static Conteo conteo(String fecha, String acceso, String bloque, int cantidad) {
        Conteo c = new Conteo();
        c.setFecha(fecha);
        c.setAcceso(acceso);
        c.setBloque(bloque);
        c.setCantidad(cantidad);
        return c;
    }
}
