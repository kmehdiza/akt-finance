package az.ingress.akt.domain;

import az.ingress.akt.domain.enums.RelativeType;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"debtor", "relatives"})
@EqualsAndHashCode(exclude = {"debtor", "relatives"})
@NoArgsConstructor
@Table(name = Person.TABLE_NAME, indexes = {
        @Index(columnList = Person.FIN_CODE_COLUMN, name = Person.FIN_CODE_INDEX, unique = true)
})
public class Person {

    public static final String TABLE_NAME = "person";
    public static final String FIN_CODE_COLUMN = "fin_code";
    public static final String INDEX = "_index";
    public static final String FIN_CODE_INDEX = FIN_CODE_COLUMN + INDEX;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RelativeType relativeType;

    private String fullName;

    @Column(name = FIN_CODE_COLUMN)
    private String finCode;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "debtor_id")
    private Person debtor;

    @OneToMany(mappedBy = "debtor")
    private Set<Person> relatives = new HashSet<>();

    private String idImage1;

    private String idImage2;

    private String signatureImage;

    private String voen;

    @NotNull
    private String mobilePhone1;

    @NotNull
    private String mobilePhone2;
}

