package az.ingress.akt.repository;

import az.ingress.akt.domain.Loan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByIdAndAgentUsername(Long id, String agentUsername);

}
