package mx.edu.utez.cotizaciones.controller.renta;

import jakarta.validation.Valid;
import mx.edu.utez.cotizaciones.controller.renta.dto.RequestCotizacionRentaDTO;
import mx.edu.utez.cotizaciones.controller.renta.dto.ResponseCotizacionRentaDTO;
import mx.edu.utez.cotizaciones.service.renta.RentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/cotizaciones/rentas")
public class RentaController {

    private final RentaService rentaService;

    public RentaController(RentaService rentaService) {
        this.rentaService = rentaService;
    }

    @PostMapping
    public ResponseEntity<ResponseCotizacionRentaDTO> cotizar(@RequestBody @Valid RequestCotizacionRentaDTO payload) {
        return ResponseEntity
                .status(200)
                .body(rentaService.cotizar(payload));
    }
}
