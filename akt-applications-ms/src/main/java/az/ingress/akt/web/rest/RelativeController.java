package az.ingress.akt.web.rest;

import az.ingress.akt.domain.Person;
import az.ingress.akt.service.LoanService;
import java.util.List;
import java.util.Set;
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

    private final LoanService loanService;

    @GetMapping("/debtor/relatives/{applicationId}")
    public Set<Person> getRelatives(
            @Positive(message = APPLICATION_ID_MUST_BE_POSITIVE) @PathVariable("applicationId") Long applicationId) {
        return loanService.getRelativesByApplicationId(applicationId);
    }
}
