package az.ingress.akt.service.impl;


import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.Type;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.exception.AlreadyExistException;
import az.ingress.akt.exception.InvalidTypeException;
import az.ingress.akt.exception.NotFoundException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
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

    private final MultipartFileService multipartFileService;

    private final LoanService loanService;

    @Override
    public void createRelative(RelativeDto relativeDto,
                               List<MultipartFile> images) {
        loanService.getLoanInfo(relativeDto.getApplicationId());
        checkIfPersonExist(relativeDto);
        Person relative = relativeDtoToPerson(relativeDto, images);
        personRepository.save(relative);
    }

    @Override
    public GetRelativeDto getRelative(Long applicationId) {
        loanService.getLoanInfo(applicationId);
        Person person = getPersonByIdIfExist(applicationId);
        checkPersonType(person.getType());
        return GetRelativeDto.builder()
                .type(person.getType())
                .finCode(person.getFinCode())
                .fullName(person.getFullName())
                .build();
    }

    private void checkPersonType(Type type) {
        if (type.equals(Type.DEBTOR)) {
            throw new InvalidTypeException(
                    String.format("Type: '%s' is not relative", type));
        }
    }

    private Person getPersonByIdIfExist(Long applicationId) {
        Optional<Person> person = personRepository.findByLoanId(applicationId);
        person.orElseThrow(() -> new NotFoundException(
                String.format("Person with applicationId: '%d' does not exist ", applicationId)));
        return person.get();
    }

    private void checkIfPersonExist(RelativeDto relativeDto) {
        Optional<Person> person = personRepository
                .findByFinCodeAndLoanId(relativeDto.getFinCode(), relativeDto.getApplicationId());
        person.ifPresent(p -> {
            throw new AlreadyExistException(relativeDto.getFinCode());
        });
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
}
