package az.ingress.akt.web.rest;

import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class RelativeController {

    private static final String APPLICATION_ID_MUST_BE_POSITIVE = "id must be positive";
    private final RelativeService relativeService;

    @PostMapping(value = "/debtor/relatives", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public void createRelative(@RequestPart("relativeDto") RelativeDto relativeDto,
                               @RequestPart("images") List<MultipartFile> images) {
        relativeService.createRelative(relativeDto, images);
    }

    @GetMapping("/debtor/relatives/{applicationId}")
    public GetRelativeDto getRelative(@Positive(message = APPLICATION_ID_MUST_BE_POSITIVE)
                                      @PathVariable Long applicationId) {
        return relativeService.getRelative(applicationId);
    }

}
