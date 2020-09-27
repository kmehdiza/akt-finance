package az.ingress.akt.web.rest.exception;

public class InvalidStateException extends RuntimeException {

    public static final String MESSAGE = "You must enter CREATED step.";

    private static final long serialVersionUID = 1L;

    public InvalidStateException(String message) {
        super(message);
    }
}
