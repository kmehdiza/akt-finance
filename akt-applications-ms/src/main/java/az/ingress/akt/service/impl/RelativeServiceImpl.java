package az.ingress.akt.service.impl;


import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class RelativeServiceImpl implements RelativeService {

    private final PersonRepository personRepository;

    private final LoanService loanService;

    @Override
    public List<GetRelativeDto> getRelatives(long applicationId, PersonType personType, int page, int size) {
        loanService.checkLoanById(applicationId);
        checkIfPersonExist(applicationId);
        Page<Person> personPage =
                personRepository.findByLoanIdAndPersonTypeIsNot(applicationId, personType, PageRequest.of(page, size));
        return personPageToGetRelariveDtoList(personPage);
    }

    private List<GetRelativeDto> personPageToGetRelariveDtoList(Page<Person> personPage) {
        ModelMapper modelMapper = new ModelMapper();
        return personPage.stream()
                .map(person -> modelMapper.map(person, GetRelativeDto.class))
                .collect(Collectors.toList());
    }

    private void checkIfPersonExist(long applicationId) {
        personRepository.findByLoanId(applicationId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Person with applicationId: '%d' does not exist ", applicationId)));
    }
}
