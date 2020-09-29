package az.ingress.akt.service.impl;

import az.ingress.akt.client.UserManagementClient;
import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.RelativeType;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.DebtorDto;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.dto.PersonDto;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.web.rest.exception.DebtorAlreadyExist;
import az.ingress.akt.web.rest.exception.InvalidStateException;
import az.ingress.akt.web.rest.exception.NotFoundException;
import az.ingress.akt.web.rest.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
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
    private final PersonRepository personRepository;
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
    public Set<PersonDto> getRelativesByLoanId(Long loanId) {
        Loan loan = findByIdAndAgentUsername(loanId);
        return loan.getDebtor().getRelatives().stream()
                .map(p -> mapper.map(p, PersonDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void createDebtor(Long applicationId, DebtorDto debtorDto) {
        loanRepository.findByIdAndAgentUsername(applicationId, getAgentUsername()).map(
                loan -> {
                    checkIfDebtorExist(loan);
                    checkLoanStep(loan);
                    mapper.map(debtorDto, loan);
                    loan.setStep(Step.FIRST_INFORMATIONS);
                    loanRepository.save(loan);
                    personRepository.save(debtorDtoToPerson(debtorDto));
                    return loan;
                }
        ).orElseThrow(() ->
                new NotFoundException(
                        String.format("Loan with id: '%d' " +
                                "and username: '%s' does not exist.", applicationId, getAgentUsername())));
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

    private void checkIfDebtorExist(Loan loan) {
        if (loan.getDebtor().getRelativeType().equals(RelativeType.DEBTOR)) {
            throw new DebtorAlreadyExist();
        }
    }

    private void checkLoanStep(Loan loan) {
        if (!loan.getStep().equals(Step.CREATED)) {
            throw new InvalidStateException("You must enter CREATED step.");
        }
    }

    private Person debtorDtoToPerson(DebtorDto debtorDto) {
        List<String> idImages = debtorDto.getIdImages();
        Person person = new Person();
        person.setFullName(debtorDto.getFullName());
        person.setFinCode(debtorDto.getFinCode());
        person.setIdImage1(idImages.get(0));
        person.setIdImage2(idImages.get(1));
        person.setRelativeType(RelativeType.DEBTOR);
        return person;
    }
}
