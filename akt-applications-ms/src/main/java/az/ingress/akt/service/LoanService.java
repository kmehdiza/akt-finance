package az.ingress.akt.service;

import az.ingress.akt.dto.PersonDto;
import java.util.Set;

public interface LoanService {

    Set<PersonDto> getRelativesByApplicationId(Long applicationId);

}
