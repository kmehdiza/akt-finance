package az.ingress.akt.exception.handler;

import az.ingress.akt.exception.ApplicationStepNotCorrectException;
import az.ingress.akt.exception.NotFoundException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RestController
public class GlobalExceptionHandler extends DefaultErrorAttributes {

    @ExceptionHandler(ApplicationStepNotCorrectException.class)
    public final ResponseEntity<Map<String, Object>> handleApplicationNotFoundException(
            ApplicationStepNotCorrectException ex,
            WebRequest request) {
        return ofType(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Map<String, Object>> handleApplicationNotFoundException(NotFoundException ex,
                                                                                        WebRequest request) {
        return ofType(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex,
                                                                                        WebRequest request) {
        return ofType(request, HttpStatus.BAD_REQUEST, getConstraintViolationExceptionMessage(ex));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        return ofType(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        ObjectError fieldError = ex.getBindingResult().getAllErrors().get(0);
        return ofType(request, HttpStatus.BAD_REQUEST, fieldError.getDefaultMessage());
    }

    private ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status, String message) {
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put("status", status.value());
        attributes.put("error", status.getReasonPhrase());
        attributes.put("message", message);
        attributes.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(attributes, status);
    }

    private String getConstraintViolationExceptionMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()).get(0);
    }
}

