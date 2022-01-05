package com.mattae.simal.modules.base.config;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.PrePersistListener;
import com.blazebit.persistence.view.PreRemoveListener;
import com.blazebit.persistence.view.PreUpdateListener;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.mattae.simal.modules.base.domain.views.AuditableView;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class AuditViewListenersConfiguration<T> implements PrePersistListener<T>, PreUpdateListener<T>, PreRemoveListener<T> {

    @Override
    public void preUpdate(EntityViewManager entityViewManager, EntityManager entityManager, Object view) {
        CurrentSecurityPrincipalProxy principal = ContextProvider.getBean(CurrentSecurityPrincipalProxy.class);
        if (AuditableView.class.isAssignableFrom(view.getClass())) {
            AuditableView auditable = (AuditableView) view;
            auditable.setLastModifiedDate(new Date());
            auditable.setLastModifiedBy(Objects.requireNonNullElse(principal.getPrincipalName(), "system"));
        }
    }

    @Override
    public void prePersist(EntityViewManager entityViewManager, EntityManager entityManager, Object view) {
        CurrentSecurityPrincipalProxy principal = ContextProvider.getBean(CurrentSecurityPrincipalProxy.class);
        if (AuditableView.class.isAssignableFrom(view.getClass())) {
            AuditableView auditable = (AuditableView) view;
            Date date = new Date();
            auditable.setCreatedDate(date);
            auditable.setLastModifiedDate(date);
            auditable.setLastModifiedBy(Objects.requireNonNullElse(principal.getPrincipalName(), "system"));
            auditable.setCreatedBy(Objects.requireNonNullElse(principal.getPrincipalName(), "system"));
            auditable.setArchived(false);
        }
    }

    @Override
    public boolean preRemove(EntityViewManager entityViewManager, EntityManager entityManager, Object view) {
        CurrentSecurityPrincipalProxy principal = ContextProvider.getBean(CurrentSecurityPrincipalProxy.class);
        if (AuditableView.class.isAssignableFrom(view.getClass())) {
            AuditableView auditable = (AuditableView) view;
            auditable.setLastModifiedDate(new Date());
            auditable.setLastModifiedBy(Objects.requireNonNullElse(principal.getPrincipalName(), "system"));
            auditable.setArchived(true);
        }
        return false;
    }
}
