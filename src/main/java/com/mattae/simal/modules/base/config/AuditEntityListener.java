package com.mattae.simal.modules.base.config;

import com.foreach.across.modules.hibernate.business.AuditableEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.Objects;

import static com.mattae.simal.modules.base.config.AuditViewListenersConfiguration.getPrincipal;

public class AuditEntityListener {

    @PrePersist
    private void beforeAnyPersist(Object entity) {
        if (AuditableEntity.class.isAssignableFrom(entity.getClass())) {
            AuditableEntity auditable = (AuditableEntity) entity;
            Date date = new Date();
            auditable.setCreatedDate(date);
            auditable.setLastModifiedDate(date);
            auditable.setLastModifiedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
            auditable.setCreatedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
        }
    }

    @PreUpdate
    private void beforeAnyUpdate(Object entity) {
        if (AuditableEntity.class.isAssignableFrom(entity.getClass())) {
            AuditableEntity auditable = (AuditableEntity) entity;
            auditable.setLastModifiedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
            auditable.setLastModifiedDate(new Date());
        }
    }

    @PreRemove
    private void beforeAnyRemove(Object entity) {
        if (AuditableEntity.class.isAssignableFrom(entity.getClass())) {
            AuditableEntity auditable = (AuditableEntity) entity;
            auditable.setLastModifiedDate(new Date());
            auditable.setLastModifiedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
        }
    }
}
