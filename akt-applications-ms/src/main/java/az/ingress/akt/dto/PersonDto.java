package az.ingress.akt.dto;

import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.RelativeType;
import lombok.Data;

@Data
public class PersonDto {

    private Long id;

    private RelativeType relativeType;

    private String fullName;

    private String finCode;

    private String idImage1;

    private String idImage2;

    private String signatureImage;
}
