package az.ingress.akt.service.impl;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.Step;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.exception.ApplicationNotFoundException;
import az.ingress.akt.exception.ApplicationStepException;
import az.ingress.akt.exception.ImagesCountException;
import az.ingress.akt.exception.PersonByFinCodeAlreadyExistException;
import az.ingress.akt.exception.UserNotFoundException;
import az.ingress.akt.repository.LoanRepository;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.security.SecurityUtils;
import az.ingress.akt.service.MultipartFileService;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@Service
public class RelativeServiceImpl implements RelativeService {

    private final PersonRepository personRepository;

    private final LoanRepository loanRepository;

    private final SecurityUtils securityUtils;

    private final MultipartFileService multipartFileService;

    @Override
    public void createRelative(RelativeDto relativeDto,
                               List<MultipartFile> file) {
        checkFileCount(file);
        Loan loan = checkIfLoanExist(relativeDto.getApplicationId(), getAgentUsername());
        checkLoanStep(loan);
        checkIfPersonExist(relativeDto);
        Person relative = relativeDtoToPerson(relativeDto, file);
        personRepository.save(relative);
    }

    private void checkLoanStep(Loan loan) {
        if (!(loan.getStep().equals(Step.FIRST_INFORMATIONS))) {
            throw new ApplicationStepException("You must enter the first information to continue any further");
        }
    }

    private void checkIfPersonExist(RelativeDto relativeDto) {
        Optional<Person> person = personRepository.findByFinCode(relativeDto.getFinCode());
        person.ifPresent(p -> {
            throw new PersonByFinCodeAlreadyExistException(relativeDto.getFinCode());
        });
    }

    private void checkFileCount(List<MultipartFile> file) {
        int count = 2;
        if (file.size() != count) {
            throw new ImagesCountException("Two images required : Front and and backside of your ID");
        }
    }

    private Person relativeDtoToPerson(RelativeDto relativeDto,
                                       List<MultipartFile> images) {
        List<String> imagesUrl = multipartFileService.uploadImages(images);
        return Person.builder()
                .fullName(relativeDto.getFullName())
                .finCode(relativeDto.getFinCode())
                .type(relativeDto.getType())
                .idImage1(imagesUrl.get(0))
                .idImage2(imagesUrl.get(1))
                .build();
    }

    private String getAgentUsername() {
        return securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new UserNotFoundException("Agent username not found"));
    }

    private Loan checkIfLoanExist(Long id, String agentUsername) {
        return loanRepository.findByIdAndAgentUsername(id, agentUsername)
                .orElseThrow(() -> new ApplicationNotFoundException(id, agentUsername));
    }

}
