package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.domain.enums.Type;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.exception.ApplicationNotFoundException;
import az.ingress.akt.exception.ApplicationStepException;
import az.ingress.akt.exception.ImagesCountException;
import az.ingress.akt.exception.PersonByFinCodeAlreadyExistException;
import az.ingress.akt.exception.UserNotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.MultipartFileService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class RelativeServiceImplTest {

    public static final String DUMMY_IMAGE_JPG = "image1.jpg";
    public static final String DUMMY_IMAGE_ORIGINAL_NAME = "/original/name1";
    public static final String DUMMY_CHARSET_NAME = "UTF-8";
    public static final String DUMMY_USERNAME = "username";
    public static final String DUMMY_CONTENT_TYPE = null;
    public static final String DUMMY_CUSTOMER_FIN = "151fgf6";
    public static final String DUMMY_FULL_NAME = "James Smith";
    public static final long DUMMY_APPLICATION_ID = 1L;
    public static final String DUMMY_IMAGE_URL = "image1.jpg";
    @Mock
    private PersonRepository personRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private MultipartFileService multipartFileService;
    @Mock
    private SecurityUtils securityUtils;
    @InjectMocks
    private RelativeServiceImpl relativeService;
    private Person person;
    private RelativeDto relativeDto;
    private Loan loan;
    private MockMultipartFile image1;
    private List<MultipartFile> images;
    private List<String> imagesUrl;

    @Before
    public void setUp() throws Exception {
        image1 = new MockMultipartFile(DUMMY_IMAGE_JPG, DUMMY_IMAGE_ORIGINAL_NAME, DUMMY_CONTENT_TYPE,
                DUMMY_IMAGE_JPG.getBytes(DUMMY_CHARSET_NAME));

        images = Arrays.asList(image1, image1);
        imagesUrl = images.stream()
                .map(m -> m.toString())
                .collect(Collectors.toList());
        loan = Loan.builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .customerFin(DUMMY_CUSTOMER_FIN)
                .step(Step.FIRST_INFORMATIONS)
                .build();
        relativeDto = RelativeDto.builder()
                .fullName(DUMMY_FULL_NAME)
                .finCode(DUMMY_CUSTOMER_FIN)
                .type(Type.MOTHER)
                .applicationId(DUMMY_APPLICATION_ID)
                .build();

        person = Person.builder()
                .id(DUMMY_APPLICATION_ID)
                .fullName(relativeDto.getFullName())
                .finCode(relativeDto.getFinCode())
                .type(relativeDto.getType())
                .idImage1(DUMMY_IMAGE_URL)
                .idImage2(DUMMY_IMAGE_URL)
                .build();
    }


    @Test
    public void givenUserNotFound() {
        assertThatThrownBy(() -> relativeService.createRelative(relativeDto, images))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void givenApplicationNotFound() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        assertThatThrownBy(() -> relativeService.createRelative(relativeDto, images))
                .isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    public void givenImageCountIsNotCorrect() {
        images = Arrays.asList(image1);
        imagesUrl = images.stream().map(m -> m.toString()).collect(Collectors.toList());
        assertThatThrownBy(() -> relativeService.createRelative(relativeDto, images))
                .isInstanceOf(ImagesCountException.class);
    }

    @Test
    public void givenPersonWithFinCodeAlreadyExist() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(loanRepository.findByIdAndAgentUsername(anyLong(), anyString())).thenReturn(Optional.of(loan));
        when(personRepository.findByFinCode(anyString()))
                .thenReturn(Optional.of(person));
        assertThatThrownBy(() -> relativeService.createRelative(relativeDto, images))
                .isInstanceOf(PersonByFinCodeAlreadyExistException.class);
    }

    @Test
    public void givenApplicationStepNotCorrect() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        loan = Loan.builder()
                .customerFin(DUMMY_CUSTOMER_FIN)
                .agentUsername(DUMMY_USERNAME)
                .id(DUMMY_APPLICATION_ID)
                .step(Step.CREATED)
                .build();
        when(loanRepository.findByIdAndAgentUsername(anyLong(), anyString())).thenReturn(Optional.of(loan));
        assertThatThrownBy(() -> relativeService.createRelative(relativeDto, images))
                .isInstanceOf(ApplicationStepException.class);
    }

    @Test
    public void givenRelativeSavePerson() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(DUMMY_USERNAME));
        when(loanRepository.findByIdAndAgentUsername(anyLong(), anyString())).thenReturn(Optional.of(loan));
        when(multipartFileService.uploadImages(images)).thenReturn(imagesUrl);
        relativeService.createRelative(relativeDto, images);
        verify(personRepository).save(any());
    }
}
