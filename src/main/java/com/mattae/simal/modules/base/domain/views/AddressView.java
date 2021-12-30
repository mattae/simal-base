package com.mattae.simal.modules.base.domain.views;

import com.blazebit.persistence.view.*;
import com.mattae.simal.modules.base.domain.entities.Address;

import java.time.LocalDateTime;

@EntityView(Address.class)
@CreatableEntityView
@UpdatableEntityView
public interface AddressView extends Address.View {
    void setLine1(String line1);

    void setLine2(String line2);

    void setCity(String city);

    void setState(String state);

    void setPostalCode(String postalCode);

    void setAddressType(String addressType);

    @PostCreate
    default void init() {
        setAddressType("Residential");
    }

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
