package az.ingress.akt.web.rest.exception;

public class LoanWithTheAgentNotFoundException extends NotFoundException{

    public static final String MESSAGE = "Loan with id: '%d' and agent username: '%s' does not exist ";

    private static final long serialVersionUID = 58432132465811L;

    public LoanWithTheAgentNotFoundException(String message) {
        super(message);
    }
}
