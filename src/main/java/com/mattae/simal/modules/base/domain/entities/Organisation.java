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
@Getter
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "update organisation set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
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

    private Boolean archived = false;

    private Boolean active = true;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }
}
