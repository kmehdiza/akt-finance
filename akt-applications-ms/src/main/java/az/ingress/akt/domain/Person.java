package az.ingress.akt.domain;

import az.ingress.akt.domain.enums.PersonType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@Table(name = Person.TABLE_NAME, indexes = {
        @Index(columnList = Person.FIN_CODE_COLUMN, name = Person.FIN_CODE_INDEX, unique = true),
        @Index(columnList = Person.PERSON_TYPE_COLUMN,name = Person.PERSON_TYPE_INDEX,unique = true)
})
public class Person {

    public static final String TABLE_NAME = "person";
    public static final String FIN_CODE_COLUMN = "fin_code";
    public static final String PERSON_TYPE_COLUMN = "fin_code";
    public static final String INDEX = "_index";
    public static final String FIN_CODE_INDEX = FIN_CODE_COLUMN + INDEX;
    public static final String PERSON_TYPE_INDEX = PERSON_TYPE_COLUMN + INDEX;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type")
    private PersonType personType;

    private String fullName;

    @Column(name = "fin_code")
    private String finCode;

    private String idImage1;

    private String idImage2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Loan loan;

    private String signatureImage;
}
