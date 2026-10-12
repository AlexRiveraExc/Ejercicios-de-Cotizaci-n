package mx.edu.utez.cotizaciones.service.envio;

import mx.edu.utez.cotizaciones.controller.envio.dto.RequestCotizacionEnvioDTO;
import mx.edu.utez.cotizaciones.controller.envio.dto.ResponseCotizacionEnvioDTO;
import mx.edu.utez.cotizaciones.exception.customExceptions.CustomBadRequestException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EnvioService {

    private static final BigDecimal COSTO_BASE = new BigDecimal("80");
    private static final BigDecimal COSTO_POR_KG = new BigDecimal("12");
    private static final BigDecimal VOLUMEN_CARGO = new BigDecimal("50000");
    private static final BigDecimal CARGO_VOLUMEN = new BigDecimal("100");
    private static final BigDecimal RECARGO_EXPRESS = new BigDecimal("0.40");
    private static final BigDecimal RECARGO_MISMO_DIA = new BigDecimal("0.70");
    private static final BigDecimal VALOR_PARA_SEGURO = new BigDecimal("10000");
    private static final BigDecimal PORCENTAJE_SEGURO = new BigDecimal("0.02");

    private static final BigDecimal PESO_MAXIMO = new BigDecimal("50");
    private static final BigDecimal DIMENSION_MAXIMA = new BigDecimal("150");
    private static final BigDecimal VOLUMEN_MAXIMO = new BigDecimal("1000000");

    public ResponseCotizacionEnvioDTO cotizar(RequestCotizacionEnvioDTO p) {
        BigDecimal volumen = p.getLargoCm().multiply(p.getAnchoCm()).multiply(p.getAltoCm());

        validarPaquete(p, volumen);

        // 1 y 2. Costo base + $12 por kilogramo
        BigDecimal cargoPorPeso = redondear(COSTO_POR_KG.multiply(p.getPesoKg()));
        // 3. Cargo por volumen
        BigDecimal cargoPorVolumen = volumen.compareTo(VOLUMEN_CARGO) > 0 ? CARGO_VOLUMEN : BigDecimal.ZERO;

        BigDecimal acumulado = COSTO_BASE.add(cargoPorPeso).add(cargoPorVolumen);

        // 4 y 5. Recargo por tipo de envio sobre el costo acumulado
        BigDecimal recargoTipo = switch (p.getTipoEnvio()) {
            case "EXPRESS" -> redondear(acumulado.multiply(RECARGO_EXPRESS));
            case "MISMO_DIA" -> redondear(acumulado.multiply(RECARGO_MISMO_DIA));
            default -> BigDecimal.ZERO;
        };
        acumulado = acumulado.add(recargoTipo);

        // 6. Seguro del 2 % si el valor declarado supera $10,000
        BigDecimal seguro = p.getValorDeclarado().compareTo(VALOR_PARA_SEGURO) > 0
                ? redondear(p.getValorDeclarado().multiply(PORCENTAJE_SEGURO))
                : BigDecimal.ZERO;

        BigDecimal total = acumulado.add(seguro);

        return new ResponseCotizacionEnvioDTO(
                p.getCodigoPostal(),
                p.getTipoEnvio(),
                redondear(volumen),
                redondear(COSTO_BASE),
                cargoPorPeso,
                redondear(cargoPorVolumen),
                recargoTipo,
                seguro,
                redondear(total)
        );
    }

    private void validarPaquete(RequestCotizacionEnvioDTO p, BigDecimal volumen) {
        if (p.getPesoKg().compareTo(PESO_MAXIMO) > 0) {
            throw new CustomBadRequestException("No se aceptan paquetes que pesen mas de 50 kg");
        }
        if (p.getLargoCm().compareTo(DIMENSION_MAXIMA) > 0
                || p.getAnchoCm().compareTo(DIMENSION_MAXIMA) > 0
                || p.getAltoCm().compareTo(DIMENSION_MAXIMA) > 0) {
            throw new CustomBadRequestException("No se aceptan paquetes con alguna dimension superior a 150 cm");
        }
        if (volumen.compareTo(VOLUMEN_MAXIMO) > 0) {
            throw new CustomBadRequestException("No se aceptan paquetes con un volumen superior a 1,000,000 cm3");
        }
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
