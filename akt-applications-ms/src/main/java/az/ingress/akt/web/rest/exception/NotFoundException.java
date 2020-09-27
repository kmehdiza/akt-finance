package az.ingress.akt.web.rest.exception;

public class NotFoundException extends RuntimeException {

    public static final String MESSAGE = "Agent username not found";

    private static final long serialVersionUID = 58432132465811L;

    public NotFoundException(String message) {
        super(message);
    }
}
