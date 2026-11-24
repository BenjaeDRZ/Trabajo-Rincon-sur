package cl.duocuc.rinconsur.ms_auth.controller;

import cl.duocuc.rinconsur.ms_auth.dto.ColaboradorDto;
import cl.duocuc.rinconsur.ms_auth.dto.LoginRequest;
import cl.duocuc.rinconsur.ms_auth.repository.ColaboradorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final ColaboradorRepository repository;

    public AuthController(ColaboradorRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/test")
    public String test() {
        return "¡El microservicio ms-auth está vivo, conectado a MySQL y registrado en Eureka!";
    }

   
    @PostMapping("/login")
    public ResponseEntity<ColaboradorDto> login(@RequestBody LoginRequest request) {
        if (request == null || request.codigo() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return repository.findById(request.codigo().trim().toUpperCase())
                .filter(c -> c.getContrasena() != null && c.getContrasena().equals(request.contrasena()))
                .map(c -> ResponseEntity.ok(ColaboradorDto.de(c)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/colaboradores")
    public List<ColaboradorDto> listar() {
        return repository.findAll().stream().map(ColaboradorDto::de).toList();
    }
}
