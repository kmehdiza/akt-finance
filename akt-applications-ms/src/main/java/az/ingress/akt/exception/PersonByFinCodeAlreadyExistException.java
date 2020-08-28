package az.ingress.akt.exception;

public class PersonByFinCodeAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public PersonByFinCodeAlreadyExistException(String finCode) {
        super(String.format("Person with FIN: '%s' already exist", finCode));
    }

}
