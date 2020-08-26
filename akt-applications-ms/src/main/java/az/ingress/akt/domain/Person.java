package az.ingress.akt.domain;

import az.ingress.akt.domain.enums.Type;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String fullName;

    private String finCode;

    private String idImage1;

    private String idImage2;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Loan loan;

    private String signatureImage;
}
