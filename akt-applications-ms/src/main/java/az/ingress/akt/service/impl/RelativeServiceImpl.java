package az.ingress.akt.service.impl;


import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import az.ingress.akt.dto.RelativeDto;
import az.ingress.akt.exception.AlreadyExistException;
import az.ingress.akt.repository.PersonRepository;
import az.ingress.akt.service.LoanService;
import az.ingress.akt.service.MultipartFileService;
import az.ingress.akt.service.RelativeService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        loanService.checkLoanByIdAndStep(relativeDto.getApplicationId());
        checkIfPersonExist(relativeDto);
        Person relative = relativeDtoToPerson(relativeDto, images);
        personRepository.save(relative);
    }

    @Override
    public Page<GetRelativeDto> getRelatives(long applicationId, PersonType personType, int page, int size) {
        loanService.checkLoanById(applicationId);
        return personRepository.findByLoanIdAndPersonTypeIsNot(applicationId, personType, PageRequest.of(page, size));
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
                .personType(relativeDto.getPersonType())
                .idImage1(imagesUrl.get(0))
                .idImage2(imagesUrl.get(1))
                .build();
    }
}
