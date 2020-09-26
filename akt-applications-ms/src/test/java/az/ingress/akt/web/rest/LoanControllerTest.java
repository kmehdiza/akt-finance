package az.ingress.akt.web.rest;

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

import az.ingress.akt.dto.IdDto;
import az.ingress.akt.security.jwt.TokenProvider;
import az.ingress.akt.service.ApplicationService;
import az.ingress.akt.web.rest.exception.UserNotFoundException;
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


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    private static final String CREATE_RELATIVE_PATH = "/application";
    private static final Long DUMMY_APPLICATION_ID = 1L;
    private final IdDto idDto = new IdDto(DUMMY_APPLICATION_ID);

    @MockBean
    @SuppressWarnings({"PMD.UnusedPrivateField"})
    private TokenProvider tokenProvider;

    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenCorrectParamWhenCreateApplicationThenReturnIsOk() throws Exception {
        //Arrange
        when(applicationService.createApplication()).thenReturn(idDto);

        //Act
        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenUsernameIsNotExistWhenCreateApplicationThenExceptionThrown() throws Exception {
        //Arrange
        doThrow(new UserNotFoundException()).when(applicationService).createApplication();

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

}
