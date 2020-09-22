package az.ingress.akt.service;

import az.ingress.akt.domain.Person;
import java.util.List;

public interface LoanService {

    List<Person> getRelativesByApplicationId(Long applicationId);

}
