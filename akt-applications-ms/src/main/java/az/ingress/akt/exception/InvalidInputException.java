package az.ingress.akt.exception;

public class InvalidInputException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public InvalidInputException(String message) {
        super(message);
    }
}
