package az.ingress.akt.web.rest;

import az.ingress.akt.dto.IdDto;
import az.ingress.akt.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/application")
    public IdDto createNewApplication() {
        return applicationService.createApplication();
    }
}
