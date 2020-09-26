package az.ingress.akt.domain;

import az.ingress.akt.domain.enums.ApplicationStep;
import az.ingress.akt.domain.enums.Status;
import az.ingress.akt.domain.enums.Step;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"debtor"})
@EqualsAndHashCode(exclude = {"debtor"})
@Table(name = Loan.TABLE_NAME)
public class Loan {

    public static final String TABLE_NAME = "loan";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStep applicationStep;

    @OneToOne
    @JoinColumn(name = "debtor_id")
    private Person debtor;

    private Step step;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createDate;

    private String agentUsername;
}
