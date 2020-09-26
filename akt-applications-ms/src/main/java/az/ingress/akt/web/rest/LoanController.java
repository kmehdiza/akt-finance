package az.ingress.akt.web.rest;

import az.ingress.akt.dto.IdDto;
import az.ingress.akt.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService applicationService;

    @PostMapping("/loan")
    public IdDto createNewApplication() {
        return applicationService.createApplication();
    }
}
