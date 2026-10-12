package mx.edu.utez.cotizaciones.controller.hospedaje.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCotizacionHospedajeDTO {
    private String nombreHuesped;
    private String tipoHabitacion;
    private int numeroNoches;
    private BigDecimal costoPorNoche;
    private BigDecimal costoHospedaje;
    private BigDecimal descuentoTemporadaBaja;
    private BigDecimal cargoTemporadaAlta;
    private BigDecimal costoDesayuno;
    private BigDecimal costoEstacionamiento;
    private BigDecimal descuentoEstanciaLarga;
    private BigDecimal subtotal;
    private BigDecimal impuestoHospedaje;
    private BigDecimal total;
}
