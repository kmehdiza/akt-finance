package az.ingress.akt.exception;

public class LoanDoesNotExistException extends RuntimeException {

    public LoanDoesNotExistException() {
    }

    public LoanDoesNotExistException(String message) {
        super(message);
    }
}
