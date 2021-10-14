package com.mattae.simal.modules.base.domain.entities;

import com.mattae.simal.modules.base.domain.enumeration.PartyType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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
}
