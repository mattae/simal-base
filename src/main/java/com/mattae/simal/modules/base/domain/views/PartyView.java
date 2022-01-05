package com.mattae.simal.modules.base.domain.views;

import com.blazebit.persistence.view.*;
import com.mattae.simal.modules.base.domain.entities.Party;
import com.mattae.simal.modules.base.domain.enumeration.PartyType;

import java.time.LocalDateTime;
import java.util.Set;

@CreatableEntityView
@UpdatableEntityView
@EntityView(Party.class)
public interface PartyView extends Party.View {
    void setType(PartyType type);

    @UpdatableMapping(cascade = {CascadeType.UPDATE, CascadeType.PERSIST, CascadeType.DELETE})
    @MappingInverse(removeStrategy = InverseRemoveStrategy.REMOVE)
    Set<AddressView> getAddresses();

    void setAddresses(Set<AddressView> addresses);

    @UpdatableMapping(cascade = {CascadeType.UPDATE, CascadeType.PERSIST, CascadeType.DELETE})
    @MappingInverse(removeStrategy = InverseRemoveStrategy.REMOVE)
    Set<IdentifierView> getIdentifiers();

    void setIdentifiers(Set<IdentifierView> identifiers);

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

    @PreUpdate
    default void preUpdate() {
        setLastModifiedDate(LocalDateTime.now());
    }

    @PrePersist
    default void prePersist() {
        setArchived(false);
        setLastModifiedDate(LocalDateTime.now());
    }
}
