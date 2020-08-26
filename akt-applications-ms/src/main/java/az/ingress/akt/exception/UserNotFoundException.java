package az.ingress.akt.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public UserNotFoundException(String message) {
        super(message);
    }
}
