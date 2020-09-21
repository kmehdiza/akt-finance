package az.ingress.akt.service.impl;

import az.ingress.akt.client.UserManagementClientImpl;
import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.web.rest.errors.UserIsNotActiveException;
import az.ingress.akt.web.rest.errors.UsernameIsNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {

    private static final String DUMMY_USERNAME = "username";
    private static final Long DUMMY_APPLICATION_ID = 1L;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private UserManagementClientImpl userManagementClient;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

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
    public void whenUsernameIsNotPresentThenGetException() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() ->
                applicationService.createApplication()).isInstanceOf(UsernameIsNotFoundException.class);
    }

    @Test
    public void whenUsernameIsNotActiveThenGetException() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(userManagementClient.isUserActive(DUMMY_USERNAME)).thenReturn(false);

        //act & Assert
        assertThatThrownBy(() -> applicationService.createApplication()).isInstanceOf(UserIsNotActiveException.class);
    }

    @Test
    public void whenCreateApplicationThenReturnIdDto() {
        //Arrange
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(userManagementClient.isUserActive(DUMMY_USERNAME)).thenReturn(true);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        //Act
        IdDto returnedIdDto = applicationService.createApplication();

        // Assert
        assertThat(returnedIdDto).isEqualTo(IdDto.builder().applicationId(loan.getId()).build());
    }
}
