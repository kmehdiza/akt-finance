package az.ingress.akt.dto;

import az.ingress.akt.domain.enums.Type;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRelativeDto {

    private Type type;

    private String finCode;

    private String fullName;

}
