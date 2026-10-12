package mx.edu.utez.cotizaciones.controller.renta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCotizacionRentaDTO {
    private String nombreCliente;
    private String tipoVehiculo;
    private int diasRenta;
    private BigDecimal costoDiario;
    private BigDecimal costoRenta;
    private int kilometrosIncluidos;
    private int kilometrosAdicionales;
    private BigDecimal cargoKilometrosAdicionales;
    private BigDecimal cargoConductorJoven;
    private BigDecimal costoSeguro;
    private BigDecimal descuento;
    private BigDecimal total;
}
