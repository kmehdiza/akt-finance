package az.ingress.akt.service.impl;

import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.exception.UserNotFoundException;
import az.ingress.akt.model.Loan;
import az.ingress.akt.model.Person;
import az.ingress.akt.model.Type;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.MultipartFileService;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class RelativeServiceImpl implements RelativeService {

    private final PersonRepository personRepository;

    private final LoanRepository loanRepository;

    private final SecurityUtils securityUtils;

    private final MultipartFileService multipartFileService;

    @Override
    public void createRelative(RelativeDto relativeDto) {
        checkIfExist(relativeDto.getApplicationId(), getUsername());
        Person relative = relativeDtoToPerson(relativeDto);
        personRepository.save(relative);
    }

    private Person relativeDtoToPerson(RelativeDto relativeDto) {
        List<String> images = multipartFileService.createImages(relativeDto.getImages());
        return Person.builder()
                .fullName(relativeDto.getFullName())
                .finCode(relativeDto.getFinCode())
                .type(Type.RELATIVE)
                .idImage1(images.get(0))
                .idImage2(images.get(1))
                .build();
    }

    private String getUsername() {
        return securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new UserNotFoundException("Username not found"));
    }

    private Loan checkIfExist(Long id, String username) {
        return loanRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new UserNotFoundException(id, username));
    }

}
