package az.ingress.akt.web.rest;

import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RelativeController {

    private static final String APPLICATION_ID_MUST_BE_POSITIVE = "Id must be positive";
    private static final String INDEX_MUST_NOT_BE_NEGATIVE = "Page index must be zero or greater";
    private static final String PAGE_SIZE_MUST_BE_POSITIVE = "Page size must be positive";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String DEFAULT_PAGE_INDEX = "0";
    private static final String DEFAULT_PAGE_SIZE = "5";

    private final RelativeService relativeService;

    @GetMapping("/debtor/relatives/{applicationId}")
    public List<GetRelativeDto> getRelatives(@Positive(message = APPLICATION_ID_MUST_BE_POSITIVE)
                                            @PathVariable Long applicationId,
                                            @PositiveOrZero(message = INDEX_MUST_NOT_BE_NEGATIVE)
                                            @RequestParam(value = PAGE,
                                                    required = false, defaultValue = DEFAULT_PAGE_INDEX)
                                                    int page,
                                            @Positive(message = PAGE_SIZE_MUST_BE_POSITIVE)
                                            @RequestParam(value = SIZE,
                                                    required = false, defaultValue = DEFAULT_PAGE_SIZE)
                                                    int size) {
        Page<GetRelativeDto> relatives = relativeService.getRelatives(applicationId, PersonType.DEBTOR, page, size);
        return relatives.getContent();
    }
}
