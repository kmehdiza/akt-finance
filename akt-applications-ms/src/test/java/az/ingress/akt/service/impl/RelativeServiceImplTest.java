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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RelativeServiceImplTest {

    public static final String DUMMY_USERNAME = "username";
    public static final String DUMMY_CUSTOMER_FIN = "151fgf6";
    public static final String DUMMY_FULL_NAME = "James Smith";
    public static final long DUMMY_APPLICATION_ID = 1L;
    public static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and username: '%s' does not exist ", DUMMY_APPLICATION_ID,
                    DUMMY_USERNAME);
    public static final String PERSON_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Person with applicationId: '%d' does not exist ", DUMMY_APPLICATION_ID);
    public static final String DUMMY_IMAGE_URL = "image1.jpg";
    public static final int PAGE = 0;
    public static final int SIZE = 3;
    public static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE);
    private static final PersonType DUMMY_PERSON_TYPE = PersonType.BROTHER;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private RelativeServiceImpl relativeService;

    private Person person;
    private Loan loan;
    private final GetRelativeDto getRelativeDto = GetRelativeDto.builder()
            .personType(DUMMY_PERSON_TYPE)
            .finCode(DUMMY_CUSTOMER_FIN)
            .fullName(DUMMY_FULL_NAME)
            .build();
    private Page<GetRelativeDto> relativePage;

    @BeforeEach
    public void setUp() throws Exception {
        List<GetRelativeDto> relativeList = Arrays.asList(getRelativeDto, getRelativeDto);
        loan = Loan.builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .customerFin(DUMMY_CUSTOMER_FIN)
                .step(Step.FIRST_INFORMATIONS)
                .build();
        relativePage = new PageImpl<GetRelativeDto>(relativeList, PAGEABLE, 2);
        person = Person.builder()
                .id(DUMMY_APPLICATION_ID)
                .finCode(DUMMY_CUSTOMER_FIN)
                .personType(DUMMY_PERSON_TYPE)
                .fullName(DUMMY_FULL_NAME)
                .idImage1("image")
                .idImage2("image")
                .loan(loan)
                .build();
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
                .thenReturn(relativePage);

        assertThat(relativeService.getRelatives(DUMMY_APPLICATION_ID, DUMMY_PERSON_TYPE, PAGE, SIZE))
                .isEqualTo(relativePage);

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
