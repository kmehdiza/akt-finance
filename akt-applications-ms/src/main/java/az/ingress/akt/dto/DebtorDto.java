package az.ingress.akt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DebtorDto {

    @NotBlank(message = "You should provide both side of pictures")
    private List<String> idImages;

    @NotBlank(message = "You should provide full name")
    private String fullName;

    @NotBlank(message = "You should provide ID Fin")
    private String finCode;

    @NotBlank(message = "You should provide Mobile phone")
    private String mobilePhone1;

    private String mobilePhone2;

    @NotBlank(message = "You should provide requested loan amount")
    private Double requestedLoanAmount;

    @NotBlank(message = "You should provide requested loan duration")
    private Integer requestedLoanDuration;

    @NotBlank(message = "You should provide initial allocation")
    private String initialAllocation;

    @NotBlank(message = "You should provide initial allocation details")
    private String initialAllocationDetails;

    private String voen;
}
