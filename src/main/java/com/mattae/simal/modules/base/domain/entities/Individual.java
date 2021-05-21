package com.mattae.simal.modules.base.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Individual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Party party;

    @Embedded
    private Name name;

    @OneToMany(mappedBy = "individual", fetch = FetchType.EAGER)
    private Set<IndividualName> names;

    private String sex;

    private String gender;

    private String email;

    private String phoneNumber;

    private String photoUrl;

    private String electorate;

    private LocalDate dateOfBirth;

    private String placeOfBirth;

    private String countryOfBirth;

    private LocalDate dateOfDeath;

    private String placeOfDeath;

    private String countryOfDeath;

    private String relationshipLifecycleStatus;

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Individual))
            return false;

        Individual other = (Individual) o;

        return id != 0L && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
