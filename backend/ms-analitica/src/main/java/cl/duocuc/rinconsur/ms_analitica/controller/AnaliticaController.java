
package cl.duocuc.rinconsur.ms_analitica.controller;

import cl.duocuc.rinconsur.ms_analitica.model.Conteo;
import cl.duocuc.rinconsur.ms_analitica.model.Transaccion;
import cl.duocuc.rinconsur.ms_analitica.repository.ConteoRepository;
import cl.duocuc.rinconsur.ms_analitica.repository.TransaccionRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/analitica")
@CrossOrigin(origins = "*")
public class AnaliticaController {

    private final ConteoRepository conteoRepository;
    private final TransaccionRepository transaccionRepository;

    public AnaliticaController(
            ConteoRepository conteoRepository,
            TransaccionRepository transaccionRepository) {
        this.conteoRepository = conteoRepository;
        this.transaccionRepository = transaccionRepository;
    }





    @GetMapping("/conteos")
    public List<Conteo> obtenerConteos(
            @RequestParam(required = false) String fecha) {

        return fecha == null
                ? conteoRepository.findAll()
                : conteoRepository.findByFecha(fecha);
    }

    @GetMapping("/conteos/por-acceso")
    public Map<String, Integer> ingresosPorAcceso(
            @RequestParam String fecha) {

        Map<String, Integer> mapa = new TreeMap<>();

        for (Conteo c : conteoRepository.findByFecha(fecha)) {
            if (c.getAcceso() == null || c.getCantidad() == null) {
                continue;
            }

            mapa.merge(c.getAcceso(), c.getCantidad(), Integer::sum);
        }

        return mapa;
    }

    @PostMapping("/conteos")
    public String cargarConteos(@RequestBody List<Conteo> conteos) {

        conteoRepository.saveAll(conteos);

        return "Se cargaron " + conteos.size()
                + " bloques horarios de conteos en la BD";
    }





    @GetMapping("/transacciones")
    public List<Transaccion> obtenerTransacciones(
            @RequestParam(required = false) String fecha) {

        return fecha == null
                ? transaccionRepository.findAll()
                : transaccionRepository.findByFecha(fecha);
    }

    @PostMapping("/transacciones")
    public String cargarTransacciones(
            @RequestBody List<Transaccion> transacciones) {

        transaccionRepository.saveAll(transacciones);

        return "Se cargaron " + transacciones.size()
                + " bloques horarios de transacciones en la BD";
    }
}
