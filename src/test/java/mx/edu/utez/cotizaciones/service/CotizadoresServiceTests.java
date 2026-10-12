package mx.edu.utez.cotizaciones.service;

import mx.edu.utez.cotizaciones.controller.envio.dto.RequestCotizacionEnvioDTO;
import mx.edu.utez.cotizaciones.controller.hospedaje.dto.RequestCotizacionHospedajeDTO;
import mx.edu.utez.cotizaciones.controller.renta.dto.RequestCotizacionRentaDTO;
import mx.edu.utez.cotizaciones.exception.customExceptions.CustomBadRequestException;
import mx.edu.utez.cotizaciones.service.envio.EnvioService;
import mx.edu.utez.cotizaciones.service.hospedaje.HospedajeService;
import mx.edu.utez.cotizaciones.service.renta.RentaService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CotizadoresServiceTests {

    private final EnvioService envio = new EnvioService();
    private final RentaService renta = new RentaService();
    private final HospedajeService hospedaje = new HospedajeService();

    private static BigDecimal bd(String v) { return new BigDecimal(v); }

    private RequestCotizacionEnvioDTO envioReq(String peso, String l, String a, String h, String tipo, String valor) {
        return new RequestCotizacionEnvioDTO("62577", bd(peso), bd(l), bd(a), bd(h), tipo, bd(valor));
    }

    @Test
    void envioExpressConSeguro() {
        // 80 + 12*8.5 = 182 ; express +40 % = 254.80 ; seguro 2 % de 12000 = 240
        var r = envio.cotizar(envioReq("8.5", "40", "30", "25", "EXPRESS", "12000"));
        assertEquals(bd("494.80"), r.getTotal());
    }

    @Test
    void envioMismoDiaConCargoPorVolumen() {
        // 80 + 12*10 + 100 = 300 ; mismo dia +70 % = 510
        var r = envio.cotizar(envioReq("10", "50", "50", "25", "MISMO_DIA", "100"));
        assertEquals(bd("510.00"), r.getTotal());
    }

    @Test
    void envioRechazaPaqueteDemasiadoPesado() {
        assertThrows(CustomBadRequestException.class,
                () -> envio.cotizar(envioReq("50.1", "10", "10", "10", "ESTANDAR", "0")));
    }

    @Test
    void envioRechazaDimensionMayorA150() {
        assertThrows(CustomBadRequestException.class,
                () -> envio.cotizar(envioReq("5", "151", "10", "10", "ESTANDAR", "0")));
    }

    @Test
    void envioRechazaVolumenMayorAlMaximo() {
        assertThrows(CustomBadRequestException.class,
                () -> envio.cotizar(envioReq("5", "150", "150", "50", "ESTANDAR", "0")));
    }

    private RequestCotizacionRentaDTO rentaReq(int edad, String tipo, int dias, int km, boolean seguro) {
        return new RequestCotizacionRentaDTO("Ana Martinez", edad, tipo, dias, km, seguro);
    }

    @Test
    void rentaConKmAdicionalesCargoJovenYSeguro() {
        // 950*5=4750 ; 150 km extra*4=600 ; 15 % de 5350 = 802.50 ; seguro 900
        var r = renta.cotizar(rentaReq(24, "SUV", 5, 650, true));
        assertEquals(bd("7052.50"), r.getTotal());
    }

    @Test
    void rentaConDescuentoPorSieteDias() {
        // 550*7=3850 ; descuento 385 ; sin km extra, sin seguro, edad 30
        var r = renta.cotizar(rentaReq(30, "COMPACTO", 7, 700, false));
        assertEquals(bd("3465.00"), r.getTotal());
    }

    @Test
    void rentaRechazos() {
        assertThrows(CustomBadRequestException.class, () -> renta.cotizar(rentaReq(17, "SEDAN", 2, 100, false)));
        assertThrows(CustomBadRequestException.class, () -> renta.cotizar(rentaReq(30, "SEDAN", 31, 100, false)));
        assertThrows(CustomBadRequestException.class, () -> renta.cotizar(rentaReq(30, "SEDAN", 20, 5001, false)));
        assertThrows(CustomBadRequestException.class, () -> renta.cotizar(rentaReq(24, "CAMIONETA", 2, 100, false)));
    }

    private RequestCotizacionHospedajeDTO hospReq(String hab, int noches, int huespedes, String temp, boolean des, boolean est) {
        return new RequestCotizacionHospedajeDTO("Carlos Lopez", hab, noches, huespedes, temp, des, est);
    }

    @Test
    void hospedajeTemporadaAltaConExtras() {
        // 1100*4=4400 ; +25 % = 1100 ; desayuno 2*4*150=1200 ; est 400 ; subtotal 7100 ; imp 284 ; total 7384
        var r = hospedaje.cotizar(hospReq("DOBLE", 4, 2, "ALTA", true, true));
        assertEquals(bd("7100.00"), r.getSubtotal());
        assertEquals(bd("7384.00"), r.getTotal());
    }

    @Test
    void hospedajeTemporadaBajaConDescuentoEstanciaLarga() {
        // 700*7=4900 ; -10 % = 490 ; -8 % = 392 ; subtotal 4018 ; imp 160.72 ; total 4178.72
        var r = hospedaje.cotizar(hospReq("INDIVIDUAL", 7, 1, "BAJA", false, false));
        assertEquals(bd("4178.72"), r.getTotal());
    }

    @Test
    void hospedajeRechazos() {
        assertThrows(CustomBadRequestException.class, () -> hospedaje.cotizar(hospReq("SUITE", 31, 2, "REGULAR", false, false)));
        assertThrows(CustomBadRequestException.class, () -> hospedaje.cotizar(hospReq("INDIVIDUAL", 2, 2, "REGULAR", false, false)));
        assertThrows(CustomBadRequestException.class, () -> hospedaje.cotizar(hospReq("DOBLE", 2, 3, "REGULAR", false, false)));
        assertThrows(CustomBadRequestException.class, () -> hospedaje.cotizar(hospReq("SUITE", 2, 5, "REGULAR", false, false)));
    }
}
