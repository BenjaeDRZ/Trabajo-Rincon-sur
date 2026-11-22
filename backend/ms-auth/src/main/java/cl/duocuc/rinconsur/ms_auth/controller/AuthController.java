package cl.duocuc.rinconsur.ms_auth.controller;

import cl.duocuc.rinconsur.ms_auth.repository.ColaboradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin; // <--- ESTE IMPORT FALTABA
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private ColaboradorRepository repository;

    @GetMapping("/test")
    public String test() {
        return "¡El microservicio ms-auth está vivo, conectado a MySQL y registrado en Eureka!";
    }
}