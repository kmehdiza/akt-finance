package az.ingress.akt.service;

import az.ingress.akt.dto.RelativeDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RelativeService {

    void createRelative(RelativeDto relativeDto, List<MultipartFile> file);
}
