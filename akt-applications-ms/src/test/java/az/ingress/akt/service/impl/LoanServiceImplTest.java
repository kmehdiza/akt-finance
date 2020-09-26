package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import az.ingress.akt.client.UserManagementClient;
import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
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
    private static final Long DUMMY_APPLICATION_ID = 1L;

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
    void setUp() {
        loan = Loan
                .builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .step(Step.CREATED)
                .status(Status.ONGOING)
                .createDate(LocalDateTime.now())
                .build();
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
}
