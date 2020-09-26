package az.ingress.akt.service.impl;

import az.ingress.akt.client.UserManagementClient;
import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.dto.PersonDto;
import az.ingress.akt.web.rest.exception.NotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.web.rest.exception.UserNotFoundException;
import java.time.LocalDateTime;
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
    private final UserManagementClient userManagementClient;

    @Override
    public IdDto createApplication() {
        log.debug("Request to create new application");
        String username = securityUtils.getCurrentUserLogin().orElseThrow(UserNotFoundException::new);
        userManagementClient.checkIfUserActive(username);
        Loan loan = Loan.builder()
                .agentUsername(username)
                .step(Step.CREATED)
                .status(Status.ONGOING)
                .createDate(LocalDateTime.now())
                .build();
        return new IdDto(loanRepository.save(loan).getId());
    }

    @Override
    @Transactional
    public Set<PersonDto> getRelativesByApplicationId(Long loanId) {
        Loan loan = findByIdAndAgentUsername(loanId);
        return loan.getDebtor().getRelatives().stream()
                .map((p) -> mapper.map(p, PersonDto.class))
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
