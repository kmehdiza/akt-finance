package az.ingress.akt.service.impl;


import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.service.RelativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class RelativeServiceImpl implements RelativeService {

    private final PersonRepository personRepository;

    private final LoanService loanService;

    @Override
    public Page<GetRelativeDto> getRelatives(long applicationId, PersonType personType, int page, int size) {
        loanService.checkLoanById(applicationId);
        checkIfPersonExist(applicationId);
        return personRepository.findByLoanIdAndPersonTypeIsNot(applicationId, personType, PageRequest.of(page, size));
    }

    private void checkIfPersonExist(long applicationId) {
        personRepository.findByLoanId(applicationId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Person with applicationId: '%d' does not exist ", applicationId)));
    }
}
