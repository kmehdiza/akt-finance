package az.ingress.akt.controller;

import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.service.RelativeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RelativeController {

    private RelativeService relativeService;

    @PostMapping("/debtor/relatives")
    public void createRelative(@RequestBody RelativeDto relativeDto) {
        relativeService.createRelative(relativeDto);
    }

}
