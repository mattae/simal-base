package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.PrePersist;
import com.blazebit.persistence.view.PreRemove;
import com.blazebit.persistence.view.*;
import com.foreach.across.modules.user.business.User;
import com.mattae.simal.modules.base.domain.views.NameView;
import com.mattae.simal.modules.base.domain.views.PartyView;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.PreUpdate;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "update individual set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
@Data
@ToString(of = {"id", "name", "party"})
public class Individual {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Embedded
    private Name name;

    @OneToMany(mappedBy = "individual", cascade = CascadeType.REMOVE)
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

    @EntityView(Individual.class)
    public interface IdView {
        @IdMapping
        UUID getId();
    }

    @EntityView(Individual.class)
    public interface View extends IdView {

        String getSex();

        String getEmail();

        String getGender();

        String getPhoneNumber();

        String getPhotoUrl();

        String getPlaceOfBirth();

        LocalDate getDateOfBirth();

        String getCountryOfBirth();

        NameView getName();
    }

    @CreatableEntityView
    @EntityView(Individual.class)
    public interface CreateView extends Individual.View {

        void setSex(String sex);

        void setEmail(String email);

        void setGender(String gender);

        void setPhoneNumber(String phoneNumber);

        void setPhotoUrl(String url);

        void setPlaceOfBirth(String place);

        void setDateOfBirth(LocalDate dateOfBirth);

        void setCountryOfBirth(String country);

        @UpdatableMapping(orphanRemoval = true)
        @AllowUpdatableEntityViews
        PartyView getParty();

        void setParty(PartyView party);

        void setName(NameView name);

        Boolean getArchived();

        void setArchived(Boolean archived);

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
            setArchived(false);
            setLastModifiedDate(LocalDateTime.now());
        }
    }

    @UpdatableEntityView
    @EntityView(Individual.class)
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }
}
