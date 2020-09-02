package az.ingress.akt.dto;

import az.ingress.akt.domain.enums.PersonType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRelativeDto {

    private PersonType personType;

    private String finCode;

    private String fullName;

}
