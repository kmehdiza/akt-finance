package az.ingress.akt.domain;

import az.ingress.akt.domain.enums.Step;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@Table(name = Loan.TABLE_NAME)
public class Loan {

    public static final String TABLE_NAME = "loan_application";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Step step;

    private String agentUsername;

    private String customerFin;
}
