package com.mattae.simal.modules.base.domain.entities;

import com.mattae.simal.modules.base.domain.enumeration.PartyType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Party {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SequenceParty")
    @SequenceGenerator(
        name = "SequenceParty",
        allocationSize = 1
    )
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PartyType type = PartyType.INDIVIDUAL;

    @Builder.Default
    private String legalType = "";

    @Builder.Default
    private String displayName = "";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "PartyAddress",
        joinColumns = @JoinColumn(name = "partyId"),
        inverseJoinColumns = @JoinColumn(name = "locationId")
    )
    private Set<Address> addresses;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "PartyRole",
        joinColumns = @JoinColumn(name = "partyId"),
        inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles;

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Party))
            return false;

        Party other = (Party) o;

        return id != 0L && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
