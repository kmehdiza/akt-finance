package az.ingress.akt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import az.ingress.akt.exception.InvalidInputException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MultipartFileServiceImplTest {

    public static final String DUMMY_IMAGE_JPG = "image1.jpg";
    public static final String DUMMY_IMAGE_ORIGINAL_NAME = "/original/name1";
    public static final String DUMMY_CHARSET_NAME = "UTF-8";
    public static final String DUMMY_CONTENT_TYPE = null;

    private MockMultipartFile mockImage;
    private MultipartFileServiceImpl multipartFileService;
    private List<MultipartFile> images;
    private List<String> imagesUrl;

    @BeforeEach
    void setUp() throws UnsupportedEncodingException {
        multipartFileService = new MultipartFileServiceImpl();
        mockImage = new MockMultipartFile(DUMMY_IMAGE_JPG, DUMMY_IMAGE_ORIGINAL_NAME, DUMMY_CONTENT_TYPE,
                DUMMY_IMAGE_JPG.getBytes(DUMMY_CHARSET_NAME));
        images = Arrays.asList(mockImage, mockImage);
        imagesUrl = images.stream()
                .map(m -> m.toString())
                .collect(Collectors.toList());
    }

    @Test
    public void givenImagesSizeLessThanTwo() {
        images = Arrays.asList(mockImage);
        assertThatThrownBy(() -> multipartFileService.uploadImages(images))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    public void givenImagesSizeMoreThanTwo() {
        images = Arrays.asList(mockImage, mockImage, mockImage);
        assertThatThrownBy(() -> multipartFileService.uploadImages(images))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    public void givenImagesThenReturnUrl() {
        assertThat(multipartFileService.uploadImages(images)).isEqualTo(imagesUrl);
    }

    @Test
    public void givenImagesSizeIsOk() {
        assertThat(multipartFileService.uploadImages(images)).hasSize(2);
    }
}
