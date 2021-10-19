package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Organisation {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    private Party party;

    private String name;

    private String email;

    private String phoneNumber;

    private LocalDate establishmentDate;

    @ManyToOne
    private Organisation parent;

    @OneToMany(mappedBy = "parent")
    private Set<Organisation> subOrganisations;
}
