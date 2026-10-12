package mx.edu.utez.cotizaciones.exception.customExceptions;

/**
 * Se lanza cuando la solicitud es sintacticamente valida pero incumple
 * una regla de negocio (por ejemplo, un paquete demasiado pesado).
 */
public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException(String message) {
        super(message);
    }
}
