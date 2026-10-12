package mx.edu.utez.cotizaciones.controller.envio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCotizacionEnvioDTO {
    private String codigoPostal;
    private String tipoEnvio;
    private BigDecimal volumenCm3;
    private BigDecimal costoBase;
    private BigDecimal cargoPorPeso;
    private BigDecimal cargoPorVolumen;
    private BigDecimal recargoPorTipoEnvio;
    private BigDecimal seguro;
    private BigDecimal total;
}
