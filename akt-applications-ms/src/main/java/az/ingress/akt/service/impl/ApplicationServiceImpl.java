package az.ingress.akt.service.impl;

import java.time.LocalDateTime;
import az.ingress.akt.client.UserManagementClient;
import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.ApplicationService;
import az.ingress.akt.web.rest.errors.UserIsNotActiveException;
import az.ingress.akt.web.rest.errors.UsernameIsNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final SecurityUtils securityUtils;
    private final UserManagementClient userManagementClient;
    private final LoanRepository loanRepository;

    @Override
    public IdDto createApplication() {
        log.debug("Request to create new application");
        String username = securityUtils.getCurrentUserLogin().orElseThrow(UsernameIsNotFoundException::new);
        if (!userManagementClient.isUserActive(username)) {
            throw new UserIsNotActiveException();
        }
        Loan loan = Loan.builder()
                .agentUsername(username)
                .step(Step.CREATED)
                .status(Status.ONGOING)
                .createDate(LocalDateTime.now())
                .build();
        loanRepository.save(loan);
        return IdDto.builder().applicationId(loan.getId()).build();
    }
}
