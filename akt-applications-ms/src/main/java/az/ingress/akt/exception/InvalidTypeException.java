package az.ingress.akt.exception;

public class InvalidTypeException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public InvalidTypeException(String message) {
        super(message);
    }
}
