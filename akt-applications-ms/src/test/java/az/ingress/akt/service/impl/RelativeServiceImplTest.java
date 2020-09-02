package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.exception.AlreadyExistException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.service.MultipartFileService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
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
    private MultipartFileService multipartFileService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private RelativeServiceImpl relativeService;

    private Person person;
    private RelativeDto relativeDto;
    private Loan loan;
    private List<MultipartFile> images;
    private List<String> imagesUrl;

    @BeforeEach
    public void setUp() throws Exception {
        MockMultipartFile mockImage =
                new MockMultipartFile(DUMMY_IMAGE_JPG, DUMMY_IMAGE_ORIGINAL_NAME, DUMMY_CONTENT_TYPE,
                        DUMMY_IMAGE_JPG.getBytes(DUMMY_CHARSET_NAME));
        images = Arrays.asList(mockImage, mockImage);
        imagesUrl = getImageUrl();
        loan = Loan.builder()
                .id(DUMMY_APPLICATION_ID)
                .agentUsername(DUMMY_USERNAME)
                .customerFin(DUMMY_CUSTOMER_FIN)
                .step(Step.FIRST_INFORMATIONS)
                .build();
        relativeDto = RelativeDto.builder()
                .fullName(DUMMY_FULL_NAME)
                .finCode(DUMMY_CUSTOMER_FIN)
                .personType(PersonType.MOTHER)
                .applicationId(DUMMY_APPLICATION_ID)
                .build();
        person = Person.builder()
                .id(DUMMY_APPLICATION_ID)
                .fullName(relativeDto.getFullName())
                .finCode(relativeDto.getFinCode())
                .personType(relativeDto.getPersonType())
                .idImage1(DUMMY_IMAGE_URL)
                .idImage2(DUMMY_IMAGE_URL)
                .build();
    }

    @Test
    public void givenRelativeByFinCodeAndLoanIdThenAlreadyExistException() {
        when(loanService.checkLoanByIdAndStep(anyLong())).thenReturn(loan);
        when(personRepository.findByFinCodeAndLoanId(anyString(), anyLong()))
                .thenReturn(Optional.ofNullable(person));
        assertThatThrownBy(() -> relativeService.createRelative(relativeDto, images))
                .isInstanceOf(AlreadyExistException.class);
    }

    @Test
    public void givenRelativeSavePersonIsOk() {
        when(loanService.checkLoanByIdAndStep(anyLong())).thenReturn(loan);
        when(multipartFileService.uploadImages(images)).thenReturn(imagesUrl);
        relativeService.createRelative(relativeDto, images);
        verify(personRepository).save(any());
    }

    private List<String> getImageUrl() {
        return images.stream()
                .map(m -> m.toString())
                .collect(Collectors.toList());
    }
}
