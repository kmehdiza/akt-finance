package az.ingress.akt.service;

import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.dto.RelativeDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface RelativeService {

    void createRelative(RelativeDto relativeDto, List<MultipartFile> file);

    Page<GetRelativeDto> getRelatives(long applicationId, PersonType personType, int page, int size);

}
