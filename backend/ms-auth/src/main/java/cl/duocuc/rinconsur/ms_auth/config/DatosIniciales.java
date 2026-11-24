package cl.duocuc.rinconsur.ms_auth.config;

import cl.duocuc.rinconsur.ms_auth.model.Colaborador;
import cl.duocuc.rinconsur.ms_auth.repository.ColaboradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Carga los colaboradores del prototipo si la tabla está vacía (contraseña: 12345). */
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarColaboradores(ColaboradorRepository repository) {
        return args -> {
            if (repository.count() > 0) return;
            String pass = "12345";
            repository.saveAll(List.of(
                new Colaborador("C-01", "Carolina Fuentes Araya", pass, "15.234.871-1", "Jefa de tienda", "P-01", "carolina.fuentes@rinconsur-ficticio.ejemplo", "+56 9 0000 0011"),
                new Colaborador("C-02", "Rodrigo Saavedra Muñoz", pass, "13.987.442-0", "Subgerente comercial", "P-02", "rodrigo.saavedra@rinconsur-ficticio.ejemplo", "+56 9 0000 0012"),
                new Colaborador("C-03", "Daniela Pizarro Toro", pass, "17.654.320-5", "Supervisora de piso (turno mañana)", "P-03", "daniela.pizarro@rinconsur-ficticio.ejemplo", "+56 9 0000 0013"),
                new Colaborador("C-04", "Matías Contreras Vidal", pass, "16.443.019-7", "Supervisor de piso (turno tarde)", "P-03", "matias.contreras@rinconsur-ficticio.ejemplo", "+56 9 0000 0014"),
                new Colaborador("C-05", "Tamara Rojas Espinoza", pass, "19.876.345-4", "Vendedora, Vestuario Mujer", "P-04", "tamara.rojas@rinconsur-ficticio.ejemplo", "+56 9 0000 0015"),
                new Colaborador("C-06", "Felipe Ibarra Soto", pass, "20.145.678-9", "Vendedor, Hogar y Deco", "P-04", "felipe.ibarra@rinconsur-ficticio.ejemplo", "+56 9 0000 0016"),
                new Colaborador("C-07", "Javiera Morales Cáceres", pass, "18.322.904-4", "Cajera, Cajas Centrales", "P-04", "javiera.morales@rinconsur-ficticio.ejemplo", "+56 9 0000 0017")
            ));
        };
    }
}
