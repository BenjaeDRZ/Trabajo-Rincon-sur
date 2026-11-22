package cl.duocuc.rinconsur.ms_analitica.controller;

import cl.duocuc.rinconsur.ms_analitica.model.Conteo;
import cl.duocuc.rinconsur.ms_analitica.repository.ConteoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analitica")
@CrossOrigin(origins = "*")
public class AnaliticaController {

    private final ConteoRepository repository;

    public AnaliticaController(ConteoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/conteos")
    public List<Conteo> obtenerConteos() {
        return repository.findAll();
    }

    @PostMapping("/conteos")
    public String cargarConteos(@RequestBody List<Conteo> conteos) {
        repository.saveAll(conteos);
        return "Se cargaron " + conteos.size() + " bloques horarios en la BD";
    }
}