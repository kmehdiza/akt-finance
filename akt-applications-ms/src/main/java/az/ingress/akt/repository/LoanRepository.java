package az.ingress.akt.repository;

import az.ingress.akt.domain.Loan;
import az.ingress.akt.domain.Person;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByIdAndAgentUsername(Long id, String agentUsername);

    @EntityGraph(value = "graph.Loan.relatives")
    Set<Person> findRelativesById(Long applicationId);
}
