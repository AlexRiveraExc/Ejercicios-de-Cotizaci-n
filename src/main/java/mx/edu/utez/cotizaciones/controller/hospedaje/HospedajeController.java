package mx.edu.utez.cotizaciones.controller.hospedaje;

import jakarta.validation.Valid;
import mx.edu.utez.cotizaciones.controller.hospedaje.dto.RequestCotizacionHospedajeDTO;
import mx.edu.utez.cotizaciones.controller.hospedaje.dto.ResponseCotizacionHospedajeDTO;
import mx.edu.utez.cotizaciones.service.hospedaje.HospedajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/cotizaciones/hospedajes")
public class HospedajeController {

    private final HospedajeService hospedajeService;

    public HospedajeController(HospedajeService hospedajeService) {
        this.hospedajeService = hospedajeService;
    }

    @PostMapping
    public ResponseEntity<ResponseCotizacionHospedajeDTO> cotizar(@RequestBody @Valid RequestCotizacionHospedajeDTO payload) {
        return ResponseEntity
                .status(200)
                .body(hospedajeService.cotizar(payload));
    }
}
