package az.ingress.akt.exception.handler;

import az.ingress.akt.dto.ExceptionDto;
import az.ingress.akt.exception.ApplicationNotFoundException;
import az.ingress.akt.exception.ApplicationStepException;
import az.ingress.akt.exception.ImagesCountException;
import az.ingress.akt.exception.PersonByFinCodeAlreadyExistException;
import az.ingress.akt.exception.UserNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationStepException.class)
    public final ResponseEntity<ExceptionDto> handleApplicationNotFoundException(ApplicationStepException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ex.getMessage(),
                Calendar.getInstance());
        log.warn(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public final ResponseEntity<ExceptionDto> handleApplicationNotFoundException(ApplicationNotFoundException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                ex.getMessage(),
                Calendar.getInstance());
        log.warn(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImagesCountException.class)
    public final ResponseEntity<ExceptionDto> handleImagesCountException(ImagesCountException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ex.getMessage(),
                Calendar.getInstance());
        log.warn(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ExceptionDto> handleLoanDoesNotExistException(UserNotFoundException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                ex.getMessage(),
                Calendar.getInstance());
        log.warn(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonByFinCodeAlreadyExistException.class)
    public final ResponseEntity<ExceptionDto> handleLoanDoesNotExistException(PersonByFinCodeAlreadyExistException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ex.getMessage(),
                Calendar.getInstance());
        log.warn(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException ex)
            throws IOException {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(),
                getConstraintViolationExceptionMessage(ex),
                getConstraintViolationExceptionMessage(ex),
                Calendar.getInstance());
        log.warn(getConstraintViolationExceptionMessage(ex));
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String message = mostSpecificCause.getMessage();
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .userMessage(message)
                .code(status.value())
                .timestamp(Calendar.getInstance())
                .technicalMessage(message)
                .build();
        return new ResponseEntity(exceptionDto, headers, status);
    }

    private String getConstraintViolationExceptionMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()).get(0);
    }

}
