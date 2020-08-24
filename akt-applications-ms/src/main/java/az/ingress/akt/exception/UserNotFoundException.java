package az.ingress.akt.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public UserNotFoundException(String messages) {
        super(messages);
    }

    public UserNotFoundException(Long id, String username) {
        super(String.format("Loan with id: '%s' and username: '%s' does not exist ", id, username));
    }

}
