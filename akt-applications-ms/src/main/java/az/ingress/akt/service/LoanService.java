package az.ingress.akt.service;

import az.ingress.akt.domain.Person;
import java.util.Set;

public interface LoanService {

    Set<Person> getRelativesByApplicationId(Long applicationId);

}
