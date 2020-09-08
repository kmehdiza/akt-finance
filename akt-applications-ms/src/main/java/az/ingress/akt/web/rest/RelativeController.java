package az.ingress.akt.web.rest;

import az.ingress.akt.dto.RelativeResponseDto;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Validated
@Controller
@RequiredArgsConstructor
public class RelativeController {

    private static final String APPLICATION_ID_MUST_BE_POSITIVE = "Application Id must be positive";

    private final RelativeService relativeService;

    @GetMapping("/debtor/relatives/{applicationId}")
    public List<RelativeResponseDto> getRelatives(@Positive(message = APPLICATION_ID_MUST_BE_POSITIVE)
                                                  @PathVariable("applicationId") Long applicationId) {
        return relativeService.getRelatives(applicationId);
    }
}
