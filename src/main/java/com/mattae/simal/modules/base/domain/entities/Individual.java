package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode(of = "id")
public class Individual {
    @Id
    @GeneratedValue
    private UUID id;

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

    private LocalDate dateOfBirth;

    private String placeOfBirth;

    private String countryOfBirth;

    private LocalDate dateOfDeath;

    private String placeOfDeath;

    private String countryOfDeath;
}
