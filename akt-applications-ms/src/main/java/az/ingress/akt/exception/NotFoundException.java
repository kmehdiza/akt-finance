package az.ingress.akt.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {

    }

    public NotFoundException(String messages) {
        super(messages);
    }

    public NotFoundException(Long id, String username) {
        super(String.format("Loan with id: '%s' and username: '%s' does not exist ", id, username));
    }

}
