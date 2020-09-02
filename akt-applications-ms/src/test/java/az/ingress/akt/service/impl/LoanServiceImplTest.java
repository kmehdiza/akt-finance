package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    public static final String DUMMY_USERNAME = "username";
    public static final String DUMMY_CUSTOMER_FIN = "151fgf6";
    public static final long DUMMY_APPLICATION_ID = 1L;
    public static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and username: '%s' does not exist ", DUMMY_APPLICATION_ID,
                    DUMMY_USERNAME);
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
    public void givenLoanIdAndNoAgentUsernameThenNotFoundException() {
        assertThatThrownBy(() -> loanService.checkLoanById(DUMMY_APPLICATION_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void givenLoanIdAndAgentUserNameThenLoanNotFoundException() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        assertThatThrownBy(() -> loanService.checkLoanById(DUMMY_APPLICATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(LOAN_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenLoanIdAndAgentUserNameThenOk() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(loanRepository.findByIdAndAgentUsername(anyLong(), anyString())).thenReturn(
                Optional.ofNullable(loan));
        assertThat(loanService.checkLoanById(DUMMY_APPLICATION_ID)).isEqualTo(loan);
        verify(securityUtils).getCurrentUserLogin();
        verify(loanRepository).findByIdAndAgentUsername(DUMMY_APPLICATION_ID, DUMMY_USERNAME);
    }
}
