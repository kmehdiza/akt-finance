package az.ingress.akt.dto;

import az.ingress.akt.domain.enums.PersonType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelativeDto {

    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @NotBlank(message = "You should provide your full name")
    private String fullName;

    @NotBlank(message = "You should provide you ID FIN")
    @Size(max = 10)
    private String finCode;

    private Long applicationId;

}
