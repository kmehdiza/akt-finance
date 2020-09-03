package az.ingress.akt.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RelativeServiceImplTest {

    private static final String DUMMY_USERNAME = "username";
    private static final String DUMMY_CUSTOMER_FIN = "151fgf6";
    private static final String DUMMY_FULL_NAME = "James Smith";
    private static final long DUMMY_APPLICATION_ID = 1L;
    private static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and username: '%s' does not exist ", DUMMY_APPLICATION_ID,
                    DUMMY_USERNAME);
    private static final String PERSON_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Person with applicationId: '%d' does not exist ", DUMMY_APPLICATION_ID);
    private static final String DUMMY_IMAGE_URL = "image1.jpg";
    private static final int PAGE = 0;
    private static final int SIZE = 3;
    private static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE);
    private static final PersonType DUMMY_PERSON_TYPE = PersonType.BROTHER;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private RelativeServiceImpl relativeService;

    private Person person;
    private Loan loan;
    private List<GetRelativeDto> relativeDtoList;
    private Page<Person> personPage;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() throws Exception {
        modelMapper = new ModelMapper();
        loan = Loan.builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .customerFin(DUMMY_CUSTOMER_FIN)
                .step(Step.FIRST_INFORMATIONS)
                .build();
        person = Person.builder()
                .id(DUMMY_APPLICATION_ID)
                .finCode(DUMMY_CUSTOMER_FIN)
                .personType(DUMMY_PERSON_TYPE)
                .fullName(DUMMY_FULL_NAME)
                .idImage1(DUMMY_IMAGE_URL)
                .idImage2(DUMMY_IMAGE_URL)
                .loan(loan)
                .build();
        List<Person> personList = Arrays.asList(person, person);
        personPage = new PageImpl<Person>(personList, PAGEABLE, 2);
        relativeDtoList = personList.stream()
                .map(p -> modelMapper.map(p, GetRelativeDto.class))
                .collect(Collectors.toList());
    }

    @Test
    public void givenApplicationIdNotExist() {
        when(loanService.checkLoanById(DUMMY_APPLICATION_ID)).thenThrow(new NotFoundException(
                LOAN_NOT_FOUND_EXCEPTION_MESSAGE));

        assertThatThrownBy(
                () -> relativeService.getRelatives(DUMMY_APPLICATION_ID, PersonType.FATHER, PAGE, SIZE))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(LOAN_NOT_FOUND_EXCEPTION_MESSAGE);

        verify(loanService).checkLoanById(DUMMY_APPLICATION_ID);
    }

    @Test
    public void givenApplicationIdThenReturnRelatives() {
        when(loanService.checkLoanById(DUMMY_APPLICATION_ID)).thenReturn(loan);
        when(personRepository.findByLoanId(DUMMY_APPLICATION_ID)).thenReturn(Optional.of(person));
        when(personRepository.findByLoanIdAndPersonTypeIsNot(DUMMY_APPLICATION_ID, DUMMY_PERSON_TYPE, PAGEABLE))
                .thenReturn(personPage);

        assertThat(relativeService.getRelatives(DUMMY_APPLICATION_ID, DUMMY_PERSON_TYPE, PAGE, SIZE))
                .isEqualTo(relativeDtoList);

        verify(loanService).checkLoanById(DUMMY_APPLICATION_ID);
        verify(personRepository).findByLoanIdAndPersonTypeIsNot(DUMMY_APPLICATION_ID, DUMMY_PERSON_TYPE, PAGEABLE);
        verify(personRepository).findByLoanId(DUMMY_APPLICATION_ID);
    }

    @Test
    public void givenApplicationIdThenPersonNotFound() {
        assertThatThrownBy(() -> relativeService.getRelatives(DUMMY_APPLICATION_ID, DUMMY_PERSON_TYPE, PAGE, SIZE))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(PERSON_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}
