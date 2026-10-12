package mx.edu.utez.cotizaciones.service.hospedaje;

import mx.edu.utez.cotizaciones.controller.hospedaje.dto.RequestCotizacionHospedajeDTO;
import mx.edu.utez.cotizaciones.controller.hospedaje.dto.ResponseCotizacionHospedajeDTO;
import mx.edu.utez.cotizaciones.exception.customExceptions.CustomBadRequestException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class HospedajeService {

    private static final BigDecimal DESCUENTO_TEMPORADA_BAJA = new BigDecimal("0.10");
    private static final BigDecimal CARGO_TEMPORADA_ALTA = new BigDecimal("0.25");
    private static final BigDecimal DESAYUNO_POR_HUESPED_NOCHE = new BigDecimal("150");
    private static final BigDecimal ESTACIONAMIENTO_POR_NOCHE = new BigDecimal("100");
    private static final BigDecimal DESCUENTO_ESTANCIA_LARGA = new BigDecimal("0.08");
    private static final BigDecimal IMPUESTO_HOSPEDAJE = new BigDecimal("0.04");

    public ResponseCotizacionHospedajeDTO cotizar(RequestCotizacionHospedajeDTO p) {
        validarReservacion(p);

        BigDecimal costoPorNoche = obtenerCostoPorNoche(p.getTipoHabitacion());
        BigDecimal noches = BigDecimal.valueOf(p.getNumeroNoches());
        BigDecimal huespedes = BigDecimal.valueOf(p.getNumeroHuespedes());

        // 1. Costo del hospedaje
        BigDecimal costoHospedaje = costoPorNoche.multiply(noches);

        // 2, 3 y 4. Ajuste por temporada
        BigDecimal descuentoTemporada = BigDecimal.ZERO;
        BigDecimal cargoTemporada = BigDecimal.ZERO;
        switch (p.getTemporada()) {
            case "BAJA" -> descuentoTemporada = redondear(costoHospedaje.multiply(DESCUENTO_TEMPORADA_BAJA));
            case "ALTA" -> cargoTemporada = redondear(costoHospedaje.multiply(CARGO_TEMPORADA_ALTA));
            default -> { /* REGULAR: sin descuentos ni cargos */ }
        }

        // 5. Desayuno
        BigDecimal desayuno = Boolean.TRUE.equals(p.getIncluyeDesayuno())
                ? huespedes.multiply(noches).multiply(DESAYUNO_POR_HUESPED_NOCHE)
                : BigDecimal.ZERO;

        // 6. Estacionamiento
        BigDecimal estacionamiento = Boolean.TRUE.equals(p.getIncluyeEstacionamiento())
                ? noches.multiply(ESTACIONAMIENTO_POR_NOCHE)
                : BigDecimal.ZERO;

        // 7. Descuento adicional de 8 % por 7 noches o mas (solo sobre el costo del hospedaje)
        BigDecimal descuentoLarga = p.getNumeroNoches() >= 7
                ? redondear(costoHospedaje.multiply(DESCUENTO_ESTANCIA_LARGA))
                : BigDecimal.ZERO;

        // 8. Subtotal e impuesto de hospedaje
        BigDecimal subtotal = costoHospedaje
                .subtract(descuentoTemporada)
                .add(cargoTemporada)
                .add(desayuno)
                .add(estacionamiento)
                .subtract(descuentoLarga);
        BigDecimal impuesto = redondear(subtotal.multiply(IMPUESTO_HOSPEDAJE));

        // 9. Total
        BigDecimal total = subtotal.add(impuesto);

        return new ResponseCotizacionHospedajeDTO(
                p.getNombreHuesped(),
                p.getTipoHabitacion(),
                p.getNumeroNoches(),
                redondear(costoPorNoche),
                redondear(costoHospedaje),
                descuentoTemporada,
                cargoTemporada,
                redondear(desayuno),
                redondear(estacionamiento),
                descuentoLarga,
                redondear(subtotal),
                impuesto,
                redondear(total)
        );
    }

    private void validarReservacion(RequestCotizacionHospedajeDTO p) {
        if (p.getNumeroNoches() > 30) {
            throw new CustomBadRequestException("No se acepta la reservacion: el numero de noches supera 30");
        }
        switch (p.getTipoHabitacion()) {
            case "INDIVIDUAL" -> {
                if (p.getNumeroHuespedes() > 1) {
                    throw new CustomBadRequestException("No se acepta la reservacion: una habitacion INDIVIDUAL es para 1 persona");
                }
            }
            case "DOBLE" -> {
                if (p.getNumeroHuespedes() > 2) {
                    throw new CustomBadRequestException("No se acepta la reservacion: una habitacion DOBLE es para maximo 2 personas");
                }
            }
            case "SUITE" -> {
                if (p.getNumeroHuespedes() > 4) {
                    throw new CustomBadRequestException("No se acepta la reservacion: una SUITE es para maximo 4 personas");
                }
            }
            default -> throw new CustomBadRequestException("Tipo de habitacion no valido");
        }
    }

    private BigDecimal obtenerCostoPorNoche(String tipoHabitacion) {
        return switch (tipoHabitacion) {
            case "INDIVIDUAL" -> new BigDecimal("700");
            case "DOBLE" -> new BigDecimal("1100");
            case "SUITE" -> new BigDecimal("1800");
            default -> throw new CustomBadRequestException("Tipo de habitacion no valido");
        };
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
