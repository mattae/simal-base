package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(indexes = {@Index(name = "ORGANISATION_NAME_INDEX", columnList = "name", unique = false)})
public class Organisation {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Party party;

    private String name;

    private String email;

    private String phoneNumber;

    private LocalDate establishmentDate;

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Organisation))
            return false;

        Organisation other = (Organisation) o;

        return id != 0L && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
