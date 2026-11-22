package cl.duocuc.rinconsur.ms_analitica.controller;

import cl.duocuc.rinconsur.ms_analitica.model.Conteo;
import cl.duocuc.rinconsur.ms_analitica.repository.ConteoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/analitica")
@CrossOrigin(origins = "*")
public class AnaliticaController {

    private final ConteoRepository repository;

    public AnaliticaController(ConteoRepository repository) {
        this.repository = repository;
    }

    /** Todos los conteos, o solo los de una fecha con ?fecha=yyyy-MM-dd */
    @GetMapping("/conteos")
    public List<Conteo> obtenerConteos(@RequestParam(required = false) String fecha) {
        return fecha == null ? repository.findAll() : repository.findByFecha(fecha);
    }

    /** Ingresos totales por acceso en una fecha: {"A1": 2040, "A2": 1105, ...} */
    @GetMapping("/conteos/por-acceso")
    public Map<String, Integer> ingresosPorAcceso(@RequestParam String fecha) {
        Map<String, Integer> mapa = new TreeMap<>();
        for (Conteo c : repository.findByFecha(fecha)) {
            if (c.getAcceso() == null || c.getCantidad() == null) continue;
            mapa.merge(c.getAcceso(), c.getCantidad(), Integer::sum);
        }
        return mapa;
    }

    @PostMapping("/conteos")
    public String cargarConteos(@RequestBody List<Conteo> conteos) {
        repository.saveAll(conteos);
        return "Se cargaron " + conteos.size() + " bloques horarios en la BD";
    }
}
