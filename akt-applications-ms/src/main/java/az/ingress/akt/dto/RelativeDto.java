package az.ingress.akt.dto;

import az.ingress.akt.model.Type;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class RelativeDto {

    @Enumerated(EnumType.STRING)
    private Type type;

    private String fullName;

    private String finCode;

    private Long applicationId;

    private List<MultipartFile> images;

}
