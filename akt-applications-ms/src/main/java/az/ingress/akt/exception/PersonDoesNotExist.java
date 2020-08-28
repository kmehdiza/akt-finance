package az.ingress.akt.exception;

public class PersonDoesNotExist extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public PersonDoesNotExist(Long applicationId) {
        super(String.format("Person  with applicationId: '%s' does not exist ", applicationId));
    }
}
