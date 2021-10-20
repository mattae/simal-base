package com.mattae.simal.modules.base.domain.entities;

import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "update individual set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
public class Individual {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    private Party party;

    @Embedded
    private Name name;

    @OneToMany(mappedBy = "individual")
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

    private Boolean archived = false;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }
}
