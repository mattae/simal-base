package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.CascadeType;
import com.blazebit.persistence.view.PrePersist;
import com.blazebit.persistence.view.PreRemove;
import com.blazebit.persistence.view.*;
import com.blazebit.persistence.view.filter.ContainsIgnoreCaseFilter;
import com.blazebit.persistence.view.filter.EqualFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mattae.simal.modules.base.domain.enumeration.PartyType;
import com.mattae.simal.modules.base.domain.views.PartyView;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.PreUpdate;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "update organisation set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
@ToString(of = {"id", "name", "party"})
public class Organisation {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    private Party party;

    private String name;

    private String email;

    private String phoneNumber;

    private LocalDate establishmentDate;

    @ManyToOne
    private Organisation parent;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private Set<Organisation> subOrganisations;

    private Boolean archived = false;

    private Boolean active = true;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }

    @EntityView(Organisation.class)
    public interface IdView {
        @IdMapping
        UUID getId();
    }

    @EntityView(Organisation.class)
    public interface View extends IdView {
        @AttributeFilter(ContainsIgnoreCaseFilter.class)
        String getName();

        @AttributeFilter(ContainsIgnoreCaseFilter.class)
        String getEmail();

        @AttributeFilter(ContainsIgnoreCaseFilter.class)
        String getPhoneNumber();

        LocalDate getEstablishmentDate();

        @Mapping("party.type")
        PartyType getType();

        @AttributeFilter(EqualFilter.class)
        Boolean getActive();
    }

    @EntityView(Organisation.class)
    @CreatableEntityView
    public interface CreateView extends Organisation.View {

        void setName(String name);

        void setEmail(String email);

        void setPhoneNumber(String phoneNumber);

        void setEstablishmentDate(LocalDate date);

        //@UpdatableMapping
        Organisation.View getParent();

        void setParent(Organisation.View parent);

        @UpdatableMapping(orphanRemoval = true, cascade = CascadeType.PERSIST)
        PartyView getParty();

        void setParty(PartyView party);

        Boolean getArchived();

        void setArchived(Boolean archived);

        void setActive(Boolean active);

        LocalDateTime getLastModifiedDate();

        void setLastModifiedDate(LocalDateTime date);

        @PreRemove
        default boolean preRemove() {
            setArchived(true);
            setLastModifiedDate(LocalDateTime.now());
            return false;
        }

        @com.blazebit.persistence.view.PreUpdate
        default void preUpdate() {
            setLastModifiedDate(LocalDateTime.now());
        }

        @PrePersist
        default void prePersist() {
            getParty().setType(PartyType.ORGANISATION);
            setArchived(false);
            setActive(true);
            setLastModifiedDate(LocalDateTime.now());
        }
    }

    @EntityView(Organisation.class)
    @UpdatableEntityView
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }
}
