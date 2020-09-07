package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.RelativeResponseDto;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class RelativeServiceImplTest {

    private static final String DUMMY_USERNAME = "username";
    private static final String DUMMY_CUSTOMER_FIN = "151fgf6";
    private static final String DUMMY_FULL_NAME = "James Smith";
    private static final long DUMMY_APPLICATION_ID = 1L;
    private static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and username: '%s' does not exist ", DUMMY_APPLICATION_ID,
                    DUMMY_USERNAME);
    private static final String DUMMY_IMAGE_URL = "image1.jpg";
    private static final PersonType DUMMY_PERSON_TYPE = PersonType.BROTHER;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private LoanService loanService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RelativeServiceImpl relativeService;

    private Loan loan;
    private List<RelativeResponseDto> relativeResponseDtoList;
    private List<Person> personList;

    @BeforeEach
    public void setUp() throws Exception {
        loan = Loan.builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .customerFin(DUMMY_CUSTOMER_FIN)
                .step(Step.FIRST_INFORMATIONS)
                .build();
        Person person = Person.builder()
                .id(DUMMY_APPLICATION_ID)
                .finCode(DUMMY_CUSTOMER_FIN)
                .personType(DUMMY_PERSON_TYPE)
                .fullName(DUMMY_FULL_NAME)
                .idImage1(DUMMY_IMAGE_URL)
                .idImage2(DUMMY_IMAGE_URL)
                .loan(loan)
                .build();
        personList = Arrays.asList(person, person);
        relativeResponseDtoList = personListToRelativeResponseDtoList(personList);
    }

    @Test
    public void givenApplicationIdThenApplicationNotExist() {
        when(loanService.checkByIdAndReturnLoan(DUMMY_APPLICATION_ID)).thenThrow(new NotFoundException(
                LOAN_NOT_FOUND_EXCEPTION_MESSAGE));

        assertThatThrownBy(
                () -> relativeService.getRelatives(DUMMY_APPLICATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(LOAN_NOT_FOUND_EXCEPTION_MESSAGE);

        verify(loanService).checkByIdAndReturnLoan(DUMMY_APPLICATION_ID);
    }

    @Test
    public void givenApplicationIdThenReturnRelativesList() {
        when(loanService.checkByIdAndReturnLoan(DUMMY_APPLICATION_ID)).thenReturn(loan);
        when(personRepository.findByLoanIdAndPersonTypeIsNot(DUMMY_APPLICATION_ID, PersonType.DEBTOR))
                .thenReturn(personList);

        relativeService.getRelatives(DUMMY_APPLICATION_ID);

        assertThat(relativeService.getRelatives(DUMMY_APPLICATION_ID))
                .isEqualTo(relativeResponseDtoList);
    }

    @Test
    public void givenApplicationIdThenReturnEmptyRelativesList() {
        personList = new ArrayList<>();
        relativeResponseDtoList = personListToRelativeResponseDtoList(personList);
        when(loanService.checkByIdAndReturnLoan(DUMMY_APPLICATION_ID)).thenReturn(loan);
        when(personRepository.findByLoanIdAndPersonTypeIsNot(DUMMY_APPLICATION_ID, PersonType.DEBTOR))
                .thenReturn(personList);

        relativeService.getRelatives(DUMMY_APPLICATION_ID);

        assertThat(relativeService.getRelatives(DUMMY_APPLICATION_ID))
                .isEqualTo(relativeResponseDtoList);
    }

    private List<RelativeResponseDto> personListToRelativeResponseDtoList(List<Person> personList) {
        return personList.stream()
                .map(person -> modelMapper.map(person, RelativeResponseDto.class))
                .collect(Collectors.toList());
    }
}
