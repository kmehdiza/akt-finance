package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import az.ingress.akt.client.UserManagementClient;
import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.RelativeType;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.web.rest.exception.NotFoundException;
import az.ingress.akt.web.rest.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {

    private static final String DUMMY_USERNAME = "username";
    public static final String DUMMY_FIN_CODE = "151fgf6";
    public static final String DUMMY_FULL_NAME = "James Smith";
    public static final RelativeType DUMMY_RELATIVE_TYPE = RelativeType.BROTHER;
    public static final String DUMMY_IMAGE = "image";
    public static final String AGENT_USERNAME_NOT_FOUND_ERROR_MESSAGE = "Agent username not found";
    private static final Long DUMMY_ID = 1L;
    private static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and agent username: '%s' does not exist ", DUMMY_ID,
                    DUMMY_USERNAME);

    @Mock
    private UserManagementClient userManagementClient;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan loan;

    @BeforeEach
    @SuppressWarnings("checkstyle:methodlength")
    void setUp() {

        Person debtor = new Person();
        debtor.setId(DUMMY_ID);
        debtor.setFinCode(DUMMY_FIN_CODE);
        debtor.setFullName(DUMMY_FULL_NAME);
        debtor.setIdImage1(DUMMY_IMAGE);
        debtor.setIdImage2(DUMMY_IMAGE);
        debtor.setRelativeType(RelativeType.DEBTOR);

        loan = Loan
                .builder()
                .id(DUMMY_ID)
                .agentUsername(DUMMY_USERNAME)
                .step(Step.CREATED)
                .status(Status.ONGOING)
                .debtor(debtor)
                .createDate(LocalDateTime.now())
                .build();

        Person relative1 = new Person();
        relative1.setId(2L);
        relative1.setFinCode(DUMMY_FIN_CODE);
        relative1.setFullName(DUMMY_FULL_NAME);
        relative1.setIdImage1(DUMMY_IMAGE);
        relative1.setIdImage2(DUMMY_IMAGE);
        relative1.setDebtor(debtor);
        relative1.setRelativeType(DUMMY_RELATIVE_TYPE);

        Person relative2 = new Person();
        relative2.setId(3L);
        relative2.setFinCode("ffff542");
        relative2.setFullName(DUMMY_FULL_NAME);
        relative2.setIdImage1(DUMMY_IMAGE);
        relative2.setIdImage2(DUMMY_IMAGE);
        relative2.setDebtor(debtor);
        relative2.setRelativeType(DUMMY_RELATIVE_TYPE);

    }

    @Test
    public void givenUsernameIsNotPresentWhenCreateApplicationThenExceptionThrown() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() ->
                loanService.createApplication()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void givenCorrectParamsWhenCreateApplicationThenReturnIdDto() {
        //Arrange

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        userManagementClient.checkIfUserActive(DUMMY_USERNAME);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        //Act
        IdDto returnedIdDto = loanService.createApplication();

        // Assert
        assertThat(returnedIdDto).isEqualTo(IdDto.builder().applicationId(loan.getId()).build());
    }

    @Test
    public void givenLoanIdWhenGettingRelativesExpectAgentUsernameNotFoundErrorMessage() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> loanService.getRelativesByLoanId(DUMMY_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(AGENT_USERNAME_NOT_FOUND_ERROR_MESSAGE);

    }

    @Test
    public void givenNonExistingLoanIdWhenGettingRelativesExpectLoanNotExistErrorMessage() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        userManagementClient.checkIfUserActive(DUMMY_USERNAME);

        //Act & Assert
        assertThatThrownBy(() -> loanService.getRelativesByLoanId(DUMMY_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(LOAN_NOT_FOUND_EXCEPTION_MESSAGE);

    }

    @Test
    public void givenLoanIdWhenGettingRelativesExpectRelativeSet() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        userManagementClient.checkIfUserActive(DUMMY_USERNAME);
        when(loanRepository.findByIdAndAgentUsername(DUMMY_ID, DUMMY_USERNAME))
                .thenReturn(Optional.ofNullable(loan));

        //Act
        loanService.getRelativesByLoanId(DUMMY_ID);

        // Assert
        assertThat(loanService.getRelativesByLoanId(DUMMY_ID))
                .isEqualTo(loan.getDebtor().getRelatives());

    }
}
