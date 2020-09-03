package az.ingress.akt.service;

import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import java.util.List;

public interface RelativeService {

    List<GetRelativeDto> getRelatives(long applicationId, PersonType personType, int page, int size);

}
