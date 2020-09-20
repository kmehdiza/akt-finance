package az.ingress.akt.web.rest.errors;

import java.util.Map;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

@ControllerAdvice
public class ExceptionHandlerAdvice extends DefaultErrorAttributes {

    @ExceptionHandler(UserIsNotActiveException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistException(
            UserIsNotActiveException ex, ServletWebRequest request) {
        return ofType(request, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Map<String, Object>> ofType(ServletWebRequest request, HttpStatus status) {
        Map<String, Object> attributes = getErrorAttributes(request, false);
        attributes.put("status", status.value());
        attributes.put("error", status.getReasonPhrase());
        attributes.put("path", request.getRequest().getRequestURI());
        return new ResponseEntity<>(attributes, status);
    }
}
