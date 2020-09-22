package az.ingress.akt.domain;

import az.ingress.akt.domain.enums.ApplicationStep;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"debtor", "relatives"})
@EqualsAndHashCode(exclude = {"debtor", "relatives"})
@Table(name = Loan.TABLE_NAME)
@NamedEntityGraph(name = "graph.Loan.relatives",
        attributeNodes = @NamedAttributeNode("relatives"))
public class Loan {

    public static final String TABLE_NAME = "loan_application";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStep applicationStep;

    private String agentUsername;

    private String customerFin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor_id")
    private Person debtor;

    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Person> relatives = new HashSet<>();
}

