package az.ingress.akt.repository;

import az.ingress.akt.domain.Person;
import az.ingress.akt.domain.enums.PersonType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findByLoanIdAndPersonTypeIsNot(Long applicationId, PersonType personType);

}
