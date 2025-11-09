package reservas.peluqueria.reservas.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GestorDeErrores {
    //estamos devolviendo un 400 cuando ocurra una entityFound..
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity gestionarError404(){
        return ResponseEntity.notFound().build();

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity gestionarError400(MethodArgumentNotValidException ex){
        var errores= ex.getFieldErrors();
        return ResponseEntity.badRequest().body(errores.stream()
                .map(DatosErrorValidacion::new)
                .toList());

    }
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity tartarErrorDeValidacion(ValidacionException e){
        return ResponseEntity.badRequest().body(e.getMessage());

    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity gestionarError400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity gestionarErrorBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity gestionarErrorAccesoDenegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity gestionarError500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " +ex.getLocalizedMessage());
    }

    public record DatosErrorValidacion(
            String campo,
            String mensaje
    ){
        public DatosErrorValidacion(FieldError error){
            this(
                    error.getField(),
                    error.getDefaultMessage());
        }
    }
}
