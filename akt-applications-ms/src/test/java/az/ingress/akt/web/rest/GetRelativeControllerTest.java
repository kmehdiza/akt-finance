package az.ingress.akt.web.rest;

import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_MESSAGE;
import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_PATH;
import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_PHRASE;
import static az.ingress.akt.web.rest.HttpResponseConstants.ERROR_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.RelativeType;
import az.ingress.akt.dto.PersonDto;
import az.ingress.akt.security.jwt.TokenProvider;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.web.rest.exception.NotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(RelativeController.class)
class GetRelativeControllerTest {

    public static final String GET_RELATIVES_PATH = "/debtor/relatives/{loanId}";
    public static final long DUMMY_ID = 1L;
    public static final String DUMMY_USERNAME = "username";
    public static final String DUMMY_FIN_CODE = "151fgf6";
    public static final String DUMMY_FULL_NAME = "James Smith";
    public static final RelativeType DUMMY_RELATIVE_TYPE = RelativeType.BROTHER;
    public static final String DUMMY_IMAGE = "image";
    public static final String AGENT_USERNAME_NOT_FOUND_ERROR_MESSAGE = "Agent username not found";
    private static final String LOAN_ID_MUST_BE_POSITIVE = "Loan id must be positive";
    private static final String LOAN_NOT_FOUND_ERROR_MESSAGE =
            String.format("Loan with id: '%d' and agent username: '%s' does not exist ", DUMMY_ID,
                    DUMMY_USERNAME);

    @MockBean
    @SuppressWarnings({"PMD.UnusedPrivateField"})
    private TokenProvider tokenProvider;

    @MockBean
    private LoanService loanService;

    @Autowired
    private MockMvc mockMvc;

    private Set<PersonDto> relatives;

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


        Set<Person> personSet = new HashSet<>();
        personSet.add(relative1);
        personSet.add(relative2);

        ModelMapper modelMapper = new ModelMapper();

        relatives = personSet.stream()
                .map(p -> modelMapper.map(p, PersonDto.class))
                .collect(Collectors.toSet());
    }

    @Test
    public void givenNegativeLoanIdWhenGettingRelativesExpectConstraintViolationErrorMessage() throws Exception {

        long id = -1L;

        mockMvc.perform(get(GET_RELATIVES_PATH, id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(LOAN_ID_MUST_BE_POSITIVE)))
                .andExpect(jsonPath(ERROR_PATH, is("/debtor/relatives/-1")));
    }

    @Test
    public void givenWrongTypeMethodArgumentWhenGettingRelativesExpectMethodArgumentTypeMismatchErrorMessage()
            throws Exception {

        String stringId = "aaa";

        mockMvc.perform(get(GET_RELATIVES_PATH, stringId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(checkMisMatchedApplicationIdMessage(stringId))))
                .andExpect(jsonPath(ERROR_PATH, is("/debtor/relatives/aaa")));
    }

    @Test
    public void givenLoanIdWhenGettingRelativesExpectLoanNotFoundErrorMessage() throws Exception {

        when(loanService.getRelativesByLoanId(DUMMY_ID)).thenThrow(new NotFoundException(
                LOAN_NOT_FOUND_ERROR_MESSAGE));

        mockMvc.perform(get(GET_RELATIVES_PATH, DUMMY_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath(ERROR_PHRASE, is(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(LOAN_NOT_FOUND_ERROR_MESSAGE)))
                .andExpect(jsonPath(ERROR_PATH, is("/debtor/relatives/1")));
    }

    @Test
    public void givenLoanIdWhenGettingRelativesExpectAgentUsernameNotFoundErrorMessage() throws Exception {

        when(loanService.getRelativesByLoanId(DUMMY_ID)).thenThrow(new NotFoundException(
                AGENT_USERNAME_NOT_FOUND_ERROR_MESSAGE));

        mockMvc.perform(get(GET_RELATIVES_PATH, DUMMY_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath(ERROR_PHRASE, is(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_MESSAGE, is(AGENT_USERNAME_NOT_FOUND_ERROR_MESSAGE)))
                .andExpect(jsonPath(ERROR_PATH, is("/debtor/relatives/1")));
    }

    @Test
    public void givenLoanIdWhenGettingRelativesExpectRelativeSet() throws Exception {
        when(loanService.getRelativesByLoanId(DUMMY_ID)).thenReturn(relatives);

        assertThat(loanService.getRelativesByLoanId(DUMMY_ID)).hasSize(2);

        mockMvc.perform(get(GET_RELATIVES_PATH, DUMMY_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    private String checkMisMatchedApplicationIdMessage(String id) {
        return String.format("Failed to convert value of type 'java.lang.String' "
                + "to required type 'java.lang.Long'; nested exception "
                + "is java.lang.NumberFormatException: For input string: "
                + "\"" + "%s" + "\"", id);
    }
}
