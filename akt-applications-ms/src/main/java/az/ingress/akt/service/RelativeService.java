package az.ingress.akt.service;

import az.ingress.akt.dto.RelativeResponseDto;
import java.util.List;

public interface RelativeService {

    List<RelativeResponseDto> getRelatives(long applicationId);

}
