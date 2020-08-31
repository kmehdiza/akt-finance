package az.ingress.akt.web.rest;

import az.ingress.akt.domain.Person;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class RelativeController {

    private final RelativeService relativeService;
    private final PersonRepository personRepository;

    @PostMapping(value = "/debtor/relatives", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public void createRelative(@RequestPart("relativeDto") RelativeDto relativeDto,
                               @RequestPart("images") List<MultipartFile> images) {
        relativeService.createRelative(relativeDto, images);
    }

    @GetMapping("/debtor/relatives/{applicationId}")
    public Optional<Person> getAll(@RequestParam("applicationId") Long applicationId) {
        return personRepository.findById(applicationId);
    }

}
