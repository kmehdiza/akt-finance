package az.ingress.akt.web.rest;

import az.ingress.akt.dto.DebtorDto;
import az.ingress.akt.dto.IdDto;
import az.ingress.akt.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private static final String ID_MUST_BE_POSITIVE = "ID must be positive";

    private final LoanService applicationService;

    @PostMapping("/loan")
    public IdDto createNewApplication() {
        return applicationService.createApplication();
    }

    @PostMapping("/debtor/{applicationId}")
    public void createDebtor(
            @Positive(message = ID_MUST_BE_POSITIVE) @PathVariable("applicationId") Long applicationId,
            @RequestBody DebtorDto debtorDto
    ) {
        applicationService.createDebtor(applicationId, debtorDto);
    }
}
