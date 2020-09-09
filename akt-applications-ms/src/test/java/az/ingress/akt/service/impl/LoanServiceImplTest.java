package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    private static final String DUMMY_USERNAME = "username";
    private static final String DUMMY_CUSTOMER_FIN = "151fgf6";
    private static final long DUMMY_APPLICATION_ID = 1L;
    private static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and username: '%s' does not exist ", DUMMY_APPLICATION_ID,
                    DUMMY_USERNAME);
    public static final String USERNAME_NOT_FOUND_EXCEPTION_MESSAGE = "Agent username not found";

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan loan;

    @BeforeEach
    void setUp() {
        loan = Loan.builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .customerFin(DUMMY_CUSTOMER_FIN)
                .step(Step.FIRST_INFORMATIONS)
                .build();
    }

    @Test
    public void givenLoanIdAndNoAgentUsernameWhenLoanFindByIdExpectNotFoundException() {
        //Arrange
        when(securityUtils.getCurrentUserLogin())
                .thenThrow(new NotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));

        //Act & Assert
        assertThatThrownBy(() -> loanService.findById(DUMMY_APPLICATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenLoanWithIdNotPresentWhenLoanFindByIdAndAgentUsernameExpectLoanNotFoundException() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(loanRepository.findByIdAndAgentUsername(DUMMY_APPLICATION_ID, DUMMY_USERNAME))
                .thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> loanService.findById(DUMMY_APPLICATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(LOAN_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenLoanIdAndAgentUserNameWhenFindByIdThenOk() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(loanRepository.findByIdAndAgentUsername(anyLong(), anyString())).thenReturn(
                Optional.ofNullable(loan));

        //Act
        loanService.findById(DUMMY_APPLICATION_ID);

        //Assert
        assertThat(loanService.findById(DUMMY_APPLICATION_ID)).isEqualTo(loan);
        verify(securityUtils, times(2)).getCurrentUserLogin();
        verify(loanRepository, times(2)).findByIdAndAgentUsername(DUMMY_APPLICATION_ID, DUMMY_USERNAME);
    }
}
