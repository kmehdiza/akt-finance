package az.ingress.akt.exception.handler;

import az.ingress.akt.exception.AlreadyExistException;
import az.ingress.akt.exception.ApplicationStepException;
import az.ingress.akt.exception.InvalidInputException;
import az.ingress.akt.exception.NotFoundException;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends DefaultErrorAttributes {

    @ExceptionHandler(ApplicationStepException.class)
    public final ResponseEntity<Map<String, Object>> handleApplicationNotFoundException(ApplicationStepException ex,
                                                                                        WebRequest request) {
        return ofType(request, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Map<String, Object>> handleApplicationNotFoundException(NotFoundException ex,
                                                                                        WebRequest request) {
        return ofType(request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public final ResponseEntity<Map<String, Object>> handleInvalidInputException(InvalidInputException ex,
                                                                                 WebRequest request) {
        return ofType(request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public final ResponseEntity<Map<String, Object>> handleAlreadyExistException(AlreadyExistException ex,
                                                                                 WebRequest request) {
        return ofType(request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex,
                                                                                        WebRequest request) {
        return ofType(request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                              WebRequest request) {
        return ofType(request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status) {
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put("status", status.value());
        attributes.put("error", status.getReasonPhrase());
        attributes.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(attributes, status);
    }
}
