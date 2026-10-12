package mx.edu.utez.cotizaciones.controller.envio;

import jakarta.validation.Valid;
import mx.edu.utez.cotizaciones.controller.envio.dto.RequestCotizacionEnvioDTO;
import mx.edu.utez.cotizaciones.controller.envio.dto.ResponseCotizacionEnvioDTO;
import mx.edu.utez.cotizaciones.service.envio.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/cotizaciones/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<ResponseCotizacionEnvioDTO> cotizar(@RequestBody @Valid RequestCotizacionEnvioDTO payload) {
        return ResponseEntity
                .status(200)
                .body(envioService.cotizar(payload));
    }
}
