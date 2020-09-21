package az.ingress.akt.web.rest;

import az.ingress.akt.dto.IdDto;
import az.ingress.akt.security.jwt.TokenProvider;
import az.ingress.akt.service.ApplicationService;
import az.ingress.akt.web.rest.errors.UserIsNotActiveException;
import az.ingress.akt.web.rest.errors.UsernameIsNotFoundException;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    private static final String CREATE_RELATIVE_PATH = "/api/application";
    private static final Long DUMMY_APPLICATION_ID = 1L;
    public static final String ERROR_STATUS = "status";
    public static final String ERROR_MESSAGE = "message";
    public static final String ERROR_PATH = "path";
    public static final String ERROR_PHRASE = "error";
    public static final String ERROR_PATH_VALUE = "/api/application";


    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private IdDto idDto;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();

        idDto = IdDto
                .builder()
                .applicationId(DUMMY_APPLICATION_ID)
                .build();
    }

    @Test
    public void whenCreateApplicationThenReturnIsOk() throws Exception {
        when(applicationService.createApplication()).thenReturn(idDto);

        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8").accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    public void givenUserIsNotActiveThenExpectException() throws Exception {
        when(applicationService.createApplication()).thenThrow(UserIsNotActiveException.class);

        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(ERROR_PATH_VALUE)));
    }

    @Test
    public void givenUsernameIsNotExistThenExpectException() throws Exception {
        when(applicationService.createApplication()).thenThrow(UsernameIsNotFoundException.class);

        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_STATUS, is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath(ERROR_PHRASE, is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath(ERROR_PATH, is(ERROR_PATH_VALUE)));
    }
}
