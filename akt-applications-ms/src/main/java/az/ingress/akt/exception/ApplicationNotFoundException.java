package az.ingress.akt.exception;

public class ApplicationNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public ApplicationNotFoundException(String messages) {
        super(messages);
    }

    public ApplicationNotFoundException(Long id, String username) {
        super(String.format("Loan with id: '%s' and username: '%s' does not exist ", id, username));
    }

}
