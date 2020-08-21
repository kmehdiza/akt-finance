package az.ingress.akt.service.impl;

import az.ingress.akt.service.MultipartFileService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MultipartFileServiceImpl implements MultipartFileService {
    @Override
    public List<String> createImages(List<MultipartFile> images) {
        return images.stream()
                .map(m -> m.toString())
                .collect(Collectors.toList());
    }

}
