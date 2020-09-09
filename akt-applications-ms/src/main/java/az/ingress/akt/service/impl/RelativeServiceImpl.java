package az.ingress.akt.service.impl;

import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.RelativeResponseDto;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelativeServiceImpl implements RelativeService {

    private final PersonRepository personRepository;
    private final LoanService loanService;
    private final ModelMapper modelMapper;

    @Override
    public List<RelativeResponseDto> getRelatives(long applicationId) {
        loanService.checkByIdAndReturnLoan(applicationId);
        List<Person> personList =
                personRepository.findByLoanIdAndPersonTypeIsNot(applicationId, PersonType.DEBTOR);
        return personListToRelativeResponseDtoList(personList);
    }

    private List<RelativeResponseDto> personListToRelativeResponseDtoList(List<Person> personList) {
        return personList.stream()
                .map(person -> modelMapper.map(person, RelativeResponseDto.class))
                .collect(Collectors.toList());
    }
}
