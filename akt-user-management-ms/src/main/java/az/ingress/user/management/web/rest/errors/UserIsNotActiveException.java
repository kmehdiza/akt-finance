package az.ingress.user.management.web.rest.errors;

import org.springframework.security.core.AuthenticationException;

public class UserIsNotActiveException extends AuthenticationException {

    private static final long serialVersionUID = 3453245432534L;

    public UserIsNotActiveException(String message) {
        super(message);
    }
}
