package az.ingress.akt.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface MultipartFileService {

    List<String> createImages(List<MultipartFile> images);

}
