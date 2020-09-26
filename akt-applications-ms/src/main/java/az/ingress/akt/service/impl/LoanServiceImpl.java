package az.ingress.akt.service.impl;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.dto.PersonDto;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.LoanService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final SecurityUtils securityUtils;
    private final LoanRepository loanRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public Set<PersonDto> getRelativesByApplicationId(Long loanId) {
        Loan loan = findByIdAndAgentUsername(loanId);
        return loan.getDebtor().getRelatives().stream()
                 .map((p)->mapper.map(p, PersonDto.class))
                 .collect(Collectors.toSet());
    }

    private Loan findByIdAndAgentUsername(Long loanId) {
        return loanRepository.findByIdAndAgentUsername(loanId, getAgentUsername())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Loan with id: '%d' and agent username: '%s' does not exist ", loanId,
                                getAgentUsername())));
    }

    private String getAgentUsername() {
        return securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new NotFoundException("Agent username not found"));
    }


}
