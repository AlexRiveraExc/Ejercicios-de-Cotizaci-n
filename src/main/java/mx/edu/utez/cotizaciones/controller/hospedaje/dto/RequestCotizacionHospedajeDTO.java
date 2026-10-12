package mx.edu.utez.cotizaciones.controller.hospedaje.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCotizacionHospedajeDTO {
    @NotBlank(message = "El nombre del huesped es obligatorio")
    private String nombreHuesped;

    @NotBlank(message = "El tipo de habitacion es obligatorio")
    @Pattern(regexp = "^(INDIVIDUAL|DOBLE|SUITE)$",
            message = "El tipo de habitacion debe ser INDIVIDUAL, DOBLE o SUITE")
    private String tipoHabitacion;

    @NotNull(message = "El numero de noches es obligatorio")
    @Min(value = 1, message = "El numero de noches debe ser de al menos 1")
    private Integer numeroNoches;

    @NotNull(message = "El numero de huespedes es obligatorio")
    @Min(value = 1, message = "El numero de huespedes debe ser de al menos 1")
    private Integer numeroHuespedes;

    @NotBlank(message = "La temporada es obligatoria")
    @Pattern(regexp = "^(BAJA|REGULAR|ALTA)$",
            message = "La temporada debe ser BAJA, REGULAR o ALTA")
    private String temporada;

    @NotNull(message = "Debe indicar si incluye desayuno")
    private Boolean incluyeDesayuno;

    @NotNull(message = "Debe indicar si incluye estacionamiento")
    private Boolean incluyeEstacionamiento;
}
