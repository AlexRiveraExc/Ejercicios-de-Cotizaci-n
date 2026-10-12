package mx.edu.utez.cotizaciones.controller.renta.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCotizacionRentaDTO {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotNull(message = "La edad del conductor es obligatoria")
    @Positive(message = "La edad del conductor debe ser mayor a 0")
    private Integer edadConductor;

    @NotBlank(message = "El tipo de vehiculo es obligatorio")
    @Pattern(regexp = "^(COMPACTO|SEDAN|SUV|CAMIONETA)$",
            message = "El tipo de vehiculo debe ser COMPACTO, SEDAN, SUV o CAMIONETA")
    private String tipoVehiculo;

    @NotNull(message = "El numero de dias de renta es obligatorio")
    @Min(value = 1, message = "La renta debe ser de al menos 1 dia")
    private Integer diasRenta;

    @NotNull(message = "Los kilometros estimados son obligatorios")
    @PositiveOrZero(message = "Los kilometros estimados no pueden ser negativos")
    private Integer kilometrosEstimados;

    @NotNull(message = "Debe indicar si contrata el seguro completo")
    private Boolean seguroCompleto;
}
