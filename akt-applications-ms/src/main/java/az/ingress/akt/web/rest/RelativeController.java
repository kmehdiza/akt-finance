package az.ingress.akt.web.rest;

import az.ingress.akt.dto.PersonDto;
import az.ingress.akt.service.LoanService;
import java.util.Set;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/debtor")
@RequiredArgsConstructor
public class RelativeController {

    private static final String LOAN_ID_MUST_BE_POSITIVE = "Loan id must be positive";

    private final LoanService loanService;

    @GetMapping("/relatives/{loanId}")
    public Set<PersonDto> getRelativesByLoanId(
            @Positive(message = LOAN_ID_MUST_BE_POSITIVE)
            @PathVariable("loanId") Long loanId) {
        return loanService.getRelativesByLoanId(loanId);
    }
}
