package com.mattae.simal.modules.base.domain.entities;

import com.mattae.simal.modules.base.domain.enumeration.PartyType;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SQLDelete(sql = "update party set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
public class Party {
    @Id
    @GeneratedValue
    private UUID id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PartyType type = PartyType.INDIVIDUAL;

    @Builder.Default
    private String legalType = "";

    @Builder.Default
    private String displayName = "";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "party_address",
        joinColumns = @JoinColumn(name = "party_id"),
        inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Address> addresses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "party_id")
    private Set<Identifier> identifiers;

    private Boolean archived = false;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }
}
