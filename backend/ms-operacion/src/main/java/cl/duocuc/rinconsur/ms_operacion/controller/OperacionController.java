package cl.duocuc.rinconsur.ms_operacion.controller;

import cl.duocuc.rinconsur.ms_operacion.model.Sesion;
import cl.duocuc.rinconsur.ms_operacion.repository.SesionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/operacion")
@CrossOrigin(origins = "*")
public class OperacionController {

    private final SesionRepository repository;

    // Inyección por constructor
    public OperacionController(SesionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/sesiones")
    public List<Sesion> listarSesiones() {
        return repository.findAll();
    }

    @PostMapping("/sesiones")
    public Sesion crearSesion(@RequestBody Sesion sesion) {
        return repository.save(sesion);
    }
}