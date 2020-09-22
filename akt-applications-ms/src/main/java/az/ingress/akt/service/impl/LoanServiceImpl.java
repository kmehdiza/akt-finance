package az.ingress.akt.service.impl;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.LoanService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final SecurityUtils securityUtils;

    private final LoanRepository loanRepository;

    private final PersonRepository personRepository;

    @Override
    public List<Person> getRelativesByApplicationId(Long applicationId) {
        Loan loan = checkIfLoanExist(applicationId);
        return loanRepository.findRelativesById(applicationId).stream().collect(Collectors.toList());
//       return loan.getRelatives();
    }

    private Loan checkIfLoanExist(Long applicationId) {
        return loanRepository.findByIdAndAgentUsername(applicationId, getAgentUsername())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Loan with id: '%d' and username: '%s' does not exist ", applicationId,
                                getAgentUsername())));
    }

    private String getAgentUsername() {
        return securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new NotFoundException("Agent username not found"));
    }


}
