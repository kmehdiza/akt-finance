package az.ingress.akt.web.rest;

import az.ingress.akt.dto.DebtorDto;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.security.jwt.TokenProvider;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.web.rest.exception.DebtorAlreadyExist;
import az.ingress.akt.web.rest.exception.NotFoundException;
import az.ingress.akt.web.rest.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_MESSAGE;
import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_PATH;
import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_STATUS;
import static az.ingress.akt.web.rest.HttpResponseConstants.TIMESTAMP;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    private static final Long DUMMY_APPLICATION_ID = 1L;
    private static final String CREATE_RELATIVE_PATH = "/loan";
    private static final String CREATE_DEBTOR_PATH = "/debtor/{applicationId}";
    private static final String CREATE_DEBTOR_ERROR_PATH = "/debtor/" + Long.toString(DUMMY_APPLICATION_ID);
    public static final String DUMMY_FIN_CODE = "151fgf6";
    public static final String DUMMY_FULL_NAME = "James Smith";
    public static final String DUMMY_IMAGE = "image";
    private static final String DUMMY_INITIAL_ALLOCATION = "allocation";
    private static final String DUMMY_INITIAL_ALLOCATION_DETAIL = "allocation_details";
    private static final Double DUMMY_REQUESTED_LOAN_AMOUNT = 12.00;
    private static final Integer DUMMY_REQUESTED_LOAN_DURATION = 12;
    private static final String DUMMY_MOBILE_PHONE1 = "055";
    private static final String DUMMY_MOBILE_PHONE2 = "050";
    private static final String ID_MUST_BE_POSITIVE = "ID must be positive";
    private static final String INVALID_STATE_EXCEPTION_MESSAGE = "You must enter CREATED step.";
    private final IdDto idDto = new IdDto(DUMMY_APPLICATION_ID);


    @MockBean
    @SuppressWarnings({"PMD.UnusedPrivateField"})
    private TokenProvider tokenProvider;

    @MockBean
    private LoanService loanService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private DebtorDto debtorDto;

    @BeforeEach
    void setUp() {
        debtorDto = DebtorDto.builder()
                .fullName(DUMMY_FULL_NAME)
                .finCode(DUMMY_FIN_CODE)
                .idImages(Arrays.asList(DUMMY_IMAGE, DUMMY_IMAGE))
                .initialAllocation(DUMMY_INITIAL_ALLOCATION)
                .initialAllocationDetails(DUMMY_INITIAL_ALLOCATION_DETAIL)
                .requestedLoanAmount(DUMMY_REQUESTED_LOAN_AMOUNT)
                .requestedLoanDuration(DUMMY_REQUESTED_LOAN_DURATION)
                .mobilePhone1(DUMMY_MOBILE_PHONE1)
                .mobilePhone2(DUMMY_MOBILE_PHONE2)
                .build();
    }

    @Test
    public void givenCorrectParamWhenCreateApplicationThenReturnIsOk() throws Exception {
        //Arrange
        when(loanService.createApplication()).thenReturn(idDto);

        //Act
        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenUsernameIsNotExistWhenCreateApplicationThenExceptionThrown() throws Exception {
        //Arrange
        doThrow(new UserNotFoundException()).when(loanService).createApplication();

        //Act
        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(UserNotFoundException.MESSAGE)))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_RELATIVE_PATH)));
    }

    @Test
    public void givenAgentUsernameIsNotExistWhenCreateDebtorThenExceptionThrow() throws Exception {
        doThrow(new NotFoundException(INVALID_STATE_EXCEPTION_MESSAGE)).when(loanService)
                .createDebtor(DUMMY_APPLICATION_ID, debtorDto);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(INVALID_STATE_EXCEPTION_MESSAGE)))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenExistDebtorWhenCreateDebtorThenExceptionThrow() throws Exception {
        doThrow(DebtorAlreadyExist.class).when(loanService)
                .createDebtor(DUMMY_APPLICATION_ID, debtorDto);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenNegativeApplicationIdWhenCreateDebtorThenExceptionThrow() throws Exception {
        Long negativeId = -1L;

        mockMvc.perform(post(CREATE_DEBTOR_PATH, negativeId)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(ID_MUST_BE_POSITIVE)))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is("/debtor/-1")));
    }

    @Test
    public void givenListIdImagesIsNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setIdImages(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide both side of pictures")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenFullNameIsNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setFullName(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide full name")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenFinCodeNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setFinCode(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide ID Fin")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenMobilePhoneIsNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setMobilePhone1(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide Mobile phone")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenRequestedLoanAmountNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setRequestedLoanAmount(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide requested loan amount")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));

    }

    @Test
    public void givenRequestedLoanDurationIsNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setRequestedLoanDuration(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide requested loan duration")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));

    }

    @Test
    public void givenInitialAllocationNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setInitialAllocation(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide initial allocation")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));
    }

    @Test
    public void givenInitialAllocationDetailsNotPresentWhenCreateDebtorThenExceptionThrow() throws Exception {
        debtorDto.setInitialAllocationDetails(null);

        mockMvc.perform(post(CREATE_DEBTOR_PATH, DUMMY_APPLICATION_ID)
                .content(objectMapper.writeValueAsString(debtorDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_MESSAGE, is("You should provide initial allocation details")))
                .andExpect(jsonPath(TIMESTAMP).isNotEmpty())
                .andExpect(jsonPath(HttpResponseConstants.ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(CREATE_DEBTOR_ERROR_PATH)));

    }
}
