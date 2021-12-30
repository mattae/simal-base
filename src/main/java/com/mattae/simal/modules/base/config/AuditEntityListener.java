package com.mattae.simal.modules.base.config;

import com.foreach.across.modules.hibernate.business.AuditableEntity;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class AuditEntityListener {

    @PrePersist
    private void beforeAnyPersist(Object entity) {
        if (AuditableEntity.class.isAssignableFrom(entity.getClass())) {
            AuditableEntity auditable = (AuditableEntity) entity;
            CurrentSecurityPrincipalProxy principal = ContextProvider.getBean(CurrentSecurityPrincipalProxy.class);
            Date date = new Date();
            auditable.setCreatedDate(date);
            auditable.setLastModifiedDate(date);
            auditable.setLastModifiedBy(Objects.requireNonNullElse(principal.getPrincipalName(), "system"));
            auditable.setCreatedBy(Objects.requireNonNullElse(principal.getPrincipalName(), "system"));
        }
    }

    @PreUpdate
    private void beforeAnyUpdate(Object entity) {
        if (AuditableEntity.class.isAssignableFrom(entity.getClass())) {
            AuditableEntity auditable = (AuditableEntity) entity;
            CurrentSecurityPrincipalProxy principal = ContextProvider.getBean(CurrentSecurityPrincipalProxy.class);
            auditable.setLastModifiedBy(principal.getPrincipalName());
            auditable.setLastModifiedDate(new Date());
        }
    }

    @PreRemove
    private void beforeAnyRemove(Object entity) {
        if (AuditableEntity.class.isAssignableFrom(entity.getClass())) {
            AuditableEntity auditable = (AuditableEntity) entity;
            CurrentSecurityPrincipalProxy principal = ContextProvider.getBean(CurrentSecurityPrincipalProxy.class);
            auditable.setLastModifiedBy(principal.getPrincipalName());
        }
    }
}
