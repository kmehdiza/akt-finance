package az.ingress.akt.repository;

import az.ingress.akt.domain.Person;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByFinCode(String finCode);

    Optional<Person> findByFinCodeAndLoanId(String finCode, Long applicationId);
}
