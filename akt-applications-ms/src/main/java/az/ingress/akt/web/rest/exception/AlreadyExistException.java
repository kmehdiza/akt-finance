package az.ingress.akt.web.rest.exception;

public class AlreadyExistException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public AlreadyExistException(String message){
        super(message);
    }
}
