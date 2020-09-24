package az.ingress.akt.service.impl;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.LoanService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final SecurityUtils securityUtils;

    private final LoanRepository loanRepository;

    @Override
    public Set<Person> getRelativesByApplicationId(Long loanId) {
        log.trace("Retrieving relatives for loan {}", loanId);
        Loan loan = findByIdAndAgentUsername(loanId);
        return loan.getRelatives();
    }

    private Loan findByIdAndAgentUsername(Long loanId) {
        return loanRepository.findByIdAndAgentUsername(loanId, getAgentUsername())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Loan with id: '%d' and username: '%s' does not exist ", loanId,
                                getAgentUsername())));
    }

    private String getAgentUsername() {
        return securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new NotFoundException("Agent username not found"));
    }


}
