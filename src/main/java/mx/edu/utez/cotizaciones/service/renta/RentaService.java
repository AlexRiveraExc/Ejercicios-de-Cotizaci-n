package mx.edu.utez.cotizaciones.service.renta;

import mx.edu.utez.cotizaciones.controller.renta.dto.RequestCotizacionRentaDTO;
import mx.edu.utez.cotizaciones.controller.renta.dto.ResponseCotizacionRentaDTO;
import mx.edu.utez.cotizaciones.exception.customExceptions.CustomBadRequestException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RentaService {

    private static final int KM_INCLUIDOS_POR_DIA = 100;
    private static final BigDecimal COSTO_KM_ADICIONAL = new BigDecimal("4");
    private static final BigDecimal CARGO_CONDUCTOR_JOVEN = new BigDecimal("0.15");
    private static final BigDecimal SEGURO_POR_DIA = new BigDecimal("180");
    private static final BigDecimal DESCUENTO_RENTA_LARGA = new BigDecimal("0.10");

    public ResponseCotizacionRentaDTO cotizar(RequestCotizacionRentaDTO p) {
        validarRenta(p);

        BigDecimal costoDiario = obtenerCostoDiario(p.getTipoVehiculo());
        BigDecimal dias = BigDecimal.valueOf(p.getDiasRenta());

        // 1. Costo de la renta
        BigDecimal costoRenta = costoDiario.multiply(dias);

        // 2 y 3. Kilometros incluidos y adicionales
        int kmIncluidos = p.getDiasRenta() * KM_INCLUIDOS_POR_DIA;
        int kmAdicionales = Math.max(0, p.getKilometrosEstimados() - kmIncluidos);
        BigDecimal cargoKm = COSTO_KM_ADICIONAL.multiply(BigDecimal.valueOf(kmAdicionales));

        // 4. Cargo por conductor joven (18 a 24 anos) sobre renta + km adicionales
        BigDecimal cargoJoven = BigDecimal.ZERO;
        if (p.getEdadConductor() >= 18 && p.getEdadConductor() <= 24) {
            cargoJoven = redondear(costoRenta.add(cargoKm).multiply(CARGO_CONDUCTOR_JOVEN));
        }

        // 5. Seguro completo
        BigDecimal seguro = Boolean.TRUE.equals(p.getSeguroCompleto())
                ? SEGURO_POR_DIA.multiply(dias)
                : BigDecimal.ZERO;

        // 6. Descuento del 10 % solo sobre el costo de la renta (7 dias o mas)
        BigDecimal descuento = p.getDiasRenta() >= 7
                ? redondear(costoRenta.multiply(DESCUENTO_RENTA_LARGA))
                : BigDecimal.ZERO;

        BigDecimal total = costoRenta.add(cargoKm).add(cargoJoven).add(seguro).subtract(descuento);

        return new ResponseCotizacionRentaDTO(
                p.getNombreCliente(),
                p.getTipoVehiculo(),
                p.getDiasRenta(),
                redondear(costoDiario),
                redondear(costoRenta),
                kmIncluidos,
                kmAdicionales,
                redondear(cargoKm),
                cargoJoven,
                redondear(seguro),
                descuento,
                redondear(total)
        );
    }

    private void validarRenta(RequestCotizacionRentaDTO p) {
        if (p.getEdadConductor() < 18) {
            throw new CustomBadRequestException("No se acepta la renta: el conductor es menor de 18 anos");
        }
        if (p.getDiasRenta() > 30) {
            throw new CustomBadRequestException("No se acepta la renta: la renta supera 30 dias");
        }
        if (p.getKilometrosEstimados() > 5000) {
            throw new CustomBadRequestException("No se acepta la renta: los kilometros estimados superan 5,000");
        }
        if (p.getTipoVehiculo().equals("CAMIONETA") && p.getEdadConductor() < 25) {
            throw new CustomBadRequestException("No se acepta la renta: una CAMIONETA requiere un conductor de 25 anos o mas");
        }
    }

    private BigDecimal obtenerCostoDiario(String tipoVehiculo) {
        return switch (tipoVehiculo) {
            case "COMPACTO" -> new BigDecimal("550");
            case "SEDAN" -> new BigDecimal("700");
            case "SUV" -> new BigDecimal("950");
            case "CAMIONETA" -> new BigDecimal("1200");
            default -> throw new CustomBadRequestException("Tipo de vehiculo no valido");
        };
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
