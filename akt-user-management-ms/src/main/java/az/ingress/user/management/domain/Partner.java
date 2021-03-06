package az.ingress.user.management.domain;

import az.ingress.user.management.domain.enumeration.ProfileStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Setter
@Builder
@Table(name = "partner")
@NoArgsConstructor
@AllArgsConstructor
public class Partner {

    @Id
    private String tin;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProfileStatus status;

    @CreatedDate
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

}
