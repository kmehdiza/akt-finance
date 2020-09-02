package az.ingress.akt.repository;

import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import az.ingress.akt.dto.GetRelativeDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByFinCodeAndLoanId(String finCode, Long applicationId);

    Page<GetRelativeDto> findByLoanIdAndPersonTypeIsNot(Long applicationId, PersonType personType,
                                                        Pageable pageable);
}
