package az.ingress.akt.service;

import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import org.springframework.data.domain.Page;

public interface RelativeService {

    Page<GetRelativeDto> getRelatives(long applicationId, PersonType personType, int page, int size);

}
