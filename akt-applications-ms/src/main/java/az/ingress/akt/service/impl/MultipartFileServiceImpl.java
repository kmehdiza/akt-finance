package az.ingress.akt.service.impl;

import az.ingress.akt.exception.InvalidInputException;
import az.ingress.akt.service.MultipartFileService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MultipartFileServiceImpl implements MultipartFileService {

    @Override
    public List<String> uploadImages(List<MultipartFile> images) {
        checkFileCount(images);
        return images.stream()
                .map(m -> m.toString())
                .collect(Collectors.toList());
    }

    private void checkFileCount(List<MultipartFile> file) {
        int count = 2;
        if (file.size() != count) {
            throw new InvalidInputException("Two images required : Front and and backside of your ID");
        }
    }
}
