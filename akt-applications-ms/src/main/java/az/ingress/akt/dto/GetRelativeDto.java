package az.ingress.akt.dto;

import az.ingress.akt.domain.enums.PersonType;
import lombok.Data;

@Data
public class GetRelativeDto {

    private PersonType personType;

    private String finCode;

    private String fullName;

}
