package az.ingress.akt.web.rest.exception;

public class UserNotFoundException extends InvalidStateException {

    public static final String MESSAGE = "User not found";
    private static final long serialVersionUID = 58432132465811L;

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
