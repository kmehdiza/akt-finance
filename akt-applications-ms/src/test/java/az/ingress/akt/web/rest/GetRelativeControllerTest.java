package az.ingress.akt.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.RelativeResponseDto;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.security.jwt.TokenProvider;
import az.ingress.akt.service.RelativeService;
import java.util.Arrays;
import java.util.List;
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

    public static final String GET_RELATIVES_PATH = "/debtor/relatives/{applicationId}";
    public static final String ERROR_STATUS = "status";
    public static final String ERROR_MESSAGE = "message";
    public static final String ERROR_PATH = "path";
    public static final String ERROR_PHRASE = "error";
    public static final long DUMMY_APPLICATION_ID = 1L;
    private static final String APPLICATION_ID_MUST_BE_POSITIVE = "Application Id must be positive";
    private static final String DUMMY_USERNAME = "username";
    private static final String LOAN_NOT_FOUND_EXCEPTION_MESSAGE =
            String.format("Loan with id: '%d' and username: '%s' does not exist ", DUMMY_APPLICATION_ID,
                    DUMMY_USERNAME);
    @MockBean
    TokenProvider tokenProvider;
    @MockBean
    private RelativeService relativeService;
    @Autowired
    private MockMvc mockMvc;
    private List<RelativeResponseDto> relativeResponseDtoList;


    @BeforeEach
    void setUp() {
        Loan loan = Loan.builder()
                .id(1L)
                .agentUsername("username")
                .customerFin("55fdgr8")
                .step(Step.FIRST_INFORMATIONS)
                .build();
        Person person = Person.builder()
                .id(1L)
                .finCode("55gt1hg")
                .personType(PersonType.BROTHER)
                .fullName("John Smith")
                .idImage1("image1")
                .idImage2("image1")
                .loan(loan)
                .build();
        ModelMapper modelMapper = new ModelMapper();
        List<Person> personList = Arrays.asList(person, person);
        relativeResponseDtoList = personList.stream()
                .map(p -> modelMapper.map(p, RelativeResponseDto.class))
                .collect(Collectors.toList());
    }

    @Test
    public void givenNegativeApplicationIdExpectConstraintViolationErrorMessage() throws Exception {
        long id = -1L;
        mockMvc.perform(get(GET_RELATIVES_PATH, id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS).value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath(ERROR_PHRASE).value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath(ERROR_MESSAGE).value(APPLICATION_ID_MUST_BE_POSITIVE))
                .andExpect(jsonPath(ERROR_PATH).value("/debtor/relatives/-1"));
    }

    @Test
    public void givenWrongTypeMethodArgumentExpectMethodArgumentTypeMismatchErrorMessage() throws Exception {
        String stringId = "aaa";
        mockMvc.perform(get(GET_RELATIVES_PATH, stringId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS).value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath(ERROR_PHRASE).value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath(ERROR_MESSAGE).value(checkMisMatchedApplicationIdMessage(stringId)))
                .andExpect(jsonPath(ERROR_PATH).value("/debtor/relatives/aaa"));
    }

    @Test
    public void givenApplicationIdExpectLoanNotFoundErrorMessage() throws Exception {
        when(relativeService.getRelatives(DUMMY_APPLICATION_ID)).thenThrow(new NotFoundException(
                LOAN_NOT_FOUND_EXCEPTION_MESSAGE));

        mockMvc.perform(get(GET_RELATIVES_PATH, DUMMY_APPLICATION_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_STATUS).value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath(ERROR_PHRASE).value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath(ERROR_MESSAGE).value(LOAN_NOT_FOUND_EXCEPTION_MESSAGE))
                .andExpect(jsonPath(ERROR_PATH).value("/debtor/relatives/1"));
    }

    @Test
    public void givenAccountWithInvalidIdExpectErrorMessage() throws Exception {
        when(relativeService.getRelatives(DUMMY_APPLICATION_ID)).thenReturn(relativeResponseDtoList);

        assertThat(relativeService.getRelatives(DUMMY_APPLICATION_ID)).hasSize(2);

        mockMvc.perform(get(GET_RELATIVES_PATH, DUMMY_APPLICATION_ID)
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
