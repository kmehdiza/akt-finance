package az.ingress.akt.exception;

import az.ingress.akt.domain.enums.Type;

public class TypeIsNotRelativeException extends RuntimeException {

    private static final long serialVersionUID = 58432132465811L;

    public TypeIsNotRelativeException(Type type) {
        super(String.format("Person type is not Relative : '%s' already exist", type));

    }
}
