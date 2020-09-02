package az.ingress.akt.service.impl;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.exception.ApplicationStepException;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final SecurityUtils securityUtils;

    private final LoanRepository loanRepository;

    @Override
    public Loan getLoanInfo(Long applicationId) {
        Loan loan = checkIfLoanExist(applicationId, getAgentUsername());
        checkLoanStep(loan);
        return loan;
    }

    private Loan checkIfLoanExist(Long id, String agentUsername) {
        return loanRepository.findByIdAndAgentUsername(id, agentUsername)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Loan with id: '%d' and username: '%s' does not exist ", id, agentUsername)));
    }

    private String getAgentUsername() {
        return securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new NotFoundException("Agent username not found"));
    }

    private void checkLoanStep(Loan loan) {
        if (!(loan.getStep().equals(Step.FIRST_INFORMATIONS))) {
            throw new ApplicationStepException("You must enter the first information to continue any further");
        }
    }
}
