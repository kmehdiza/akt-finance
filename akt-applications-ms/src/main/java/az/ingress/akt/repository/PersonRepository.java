package az.ingress.akt.repository;

import az.ingress.akt.domain.Person;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @EntityGraph(value = "graph.Loan.relatives")
    Set<Person> findByLoanId(Long applicationId);
}
