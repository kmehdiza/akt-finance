package az.ingress.akt.exception;

public class LoanDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public LoanDoesNotExistException(String message) {
        super(message);
    }
}
