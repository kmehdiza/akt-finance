package az.ingress.akt.web.rest.exception;

public class DebtorAlreadyExistException extends InvalidStateException {

    public static final String MESSAGE = "Debtor already exist";

    private static final long serialVersionUID = 1L;

    public DebtorAlreadyExistException(String message) {
        super(message);
    }
}
