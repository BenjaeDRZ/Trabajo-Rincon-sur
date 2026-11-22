package cl.duocuc.rinconsur.ms_reportes.controller;

import cl.duocuc.rinconsur.ms_reportes.model.Meta;
import cl.duocuc.rinconsur.ms_reportes.repository.MetaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReportesController {

    private final MetaRepository repository;

    public ReportesController(MetaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/metas")
    public List<Meta> obtenerMetas() {
        return repository.findAll();
    }

    @PostMapping("/metas")
    public Meta crearMeta(@RequestBody Meta meta) {
        return repository.save(meta);
    }
}