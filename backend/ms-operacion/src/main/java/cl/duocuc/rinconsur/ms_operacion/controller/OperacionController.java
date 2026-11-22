package cl.duocuc.rinconsur.ms_operacion.controller;

import cl.duocuc.rinconsur.ms_operacion.model.Registro;
import cl.duocuc.rinconsur.ms_operacion.model.Sesion;
import cl.duocuc.rinconsur.ms_operacion.repository.RegistroRepository;
import cl.duocuc.rinconsur.ms_operacion.repository.SesionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operacion")
@CrossOrigin(origins = "*")
public class OperacionController {

    private final SesionRepository repository;
    private final RegistroRepository registroRepository;

    public OperacionController(SesionRepository repository, RegistroRepository registroRepository) {
        this.repository = repository;
        this.registroRepository = registroRepository;
    }

    // ---------------- Sesiones ----------------

    @GetMapping("/sesiones")
    public List<Sesion> listarSesiones() {
        return repository.findAll();
    }

    @GetMapping("/sesiones/{numero}")
    public ResponseEntity<Sesion> obtenerSesion(@PathVariable String numero) {
        return repository.findById(numero).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /** Crea o reemplaza la sesión (la app la envía completa). */
    @PostMapping("/sesiones")
    public Sesion crearSesion(@RequestBody Sesion sesion) {
        return repository.save(sesion);
    }

    /** Cambia estado (EN_CURSO, FINALIZADA, VALIDADA, RECHAZADA) y, si aplica, validador/motivo. */
    @PutMapping("/sesiones/{numero}/estado")
    public ResponseEntity<Sesion> actualizarEstado(@PathVariable String numero, @RequestBody Sesion cambios) {
        return repository.findById(numero).map(s -> {
            s.setEstado(cambios.getEstado());
            s.setValidador(cambios.getValidador());
            s.setMotivoRechazo(cambios.getMotivoRechazo());
            return ResponseEntity.ok(repository.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ---------------- Registros de personas ----------------

    @GetMapping("/sesiones/{numero}/registros")
    public List<Registro> registrosDeSesion(@PathVariable String numero) {
        return registroRepository.findBySesionAndAnuladoFalse(numero);
    }

    /** Idempotente: si ya existe (misma sesión + número) se actualiza en vez de duplicar. */
    @PostMapping("/registros")
    public Registro crearRegistro(@RequestBody Registro registro) {
        registroRepository.findBySesionAndNumero(registro.getSesion(), registro.getNumero())
                .ifPresent(existente -> registro.setId(existente.getId()));
        if (registro.getAnulado() == null) registro.setAnulado(false);
        return registroRepository.save(registro);
    }

    @PutMapping("/sesiones/{sesion}/registros/{numero}/anular")
    public ResponseEntity<Registro> anularRegistro(@PathVariable String sesion, @PathVariable String numero) {
        return registroRepository.findBySesionAndNumero(sesion, numero).map(r -> {
            r.setAnulado(true);
            return ResponseEntity.ok(registroRepository.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }
}
