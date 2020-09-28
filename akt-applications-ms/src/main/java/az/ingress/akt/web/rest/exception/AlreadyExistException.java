package az.ingress.akt.web.rest.exception;

public class AlreadyExistException extends RuntimeException {

    public static final String MESSAGE = "Debtor already exist";

    private static final long serialVersionUID = 1L;

    public AlreadyExistException(String message) {
        super(message);
    }
}
