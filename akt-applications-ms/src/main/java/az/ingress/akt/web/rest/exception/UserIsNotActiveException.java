package az.ingress.akt.web.rest.exception;

public class UserIsNotActiveException extends InvalidStateException {

    private static final long serialVersionUID = 58432132465811L;

    public UserIsNotActiveException() {
        super("The user is not active");
    }
}
