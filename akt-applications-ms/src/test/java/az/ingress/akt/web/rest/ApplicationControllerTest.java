package az.ingress.akt.web.rest;

import az.ingress.akt.dto.IdDto;
import az.ingress.akt.security.jwt.TokenProvider;
import az.ingress.akt.service.ApplicationService;
import az.ingress.akt.web.rest.errors.UsernameIsNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    private static final String CREATE_RELATIVE_PATH = "/application";
    private static final Long DUMMY_APPLICATION_ID = 1L;


    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final IdDto idDto = new IdDto(DUMMY_APPLICATION_ID);

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();
    }

    @Test
    public void givenCorrectParamWhenCreateApplicationThenReturnIsOk() throws Exception {
        when(applicationService.createApplication()).thenReturn(idDto);

        mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenUsernameIsNotExistWhenCreateApplicationThenExceptionThrown() throws Exception {
        when(applicationService.createApplication()).thenThrow(UsernameIsNotFoundException.class);

        MvcResult mvcResult = mockMvc.perform(post(CREATE_RELATIVE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();
        convertMvcResultToString(mvcResult);
    }

    private void convertMvcResultToString(MvcResult mvcResult) throws Exception {
        mvcResult.getResponse().getContentAsString();
    }
}
