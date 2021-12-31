package com.mattae.simal.modules.base.domain.views;

import com.blazebit.persistence.view.*;
import com.mattae.simal.modules.base.domain.entities.Identifier;
import com.mattae.simal.modules.base.domain.entities.Party;

import java.time.LocalDateTime;
import java.util.UUID;

@EntityView(Identifier.class)
@CreatableEntityView
@UpdatableEntityView
public interface IdentifierView extends Identifier.View {
    void setId(UUID id);

    void setValue(String value);

    void setRegister(String register);

    void setType(String type);

    void setFromDate(LocalDateTime fromDate);

    void setToDate(LocalDateTime toDate);

    Boolean getArchived();

    void setArchived(Boolean archived);

    Party.View getParty();

    void setParty(Party.View party);

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
