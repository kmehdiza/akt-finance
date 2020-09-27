package az.ingress.akt.service;

import az.ingress.akt.dto.DebtorDto;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.dto.PersonDto;
import java.util.Set;

public interface LoanService {

    IdDto createApplication();

    Set<PersonDto> getRelativesByLoanId(Long loanId);

    void createDebtor(Long applicationId, DebtorDto debtorDto);

}
