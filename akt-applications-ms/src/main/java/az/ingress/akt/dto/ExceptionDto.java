package az.ingress.akt.dto;

import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDto {
    private int code;
    private String technicalMessage;
    private String userMessage;
    private Calendar timestamp;
}
