package com.mattae.simal.modules.base.config;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.PrePersistListener;
import com.blazebit.persistence.view.PreRemoveListener;
import com.blazebit.persistence.view.PreUpdateListener;
import com.mattae.simal.modules.base.domain.views.AuditableView;
import com.mattae.simal.modules.base.services.ExtensionService;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class AuditViewListenersConfiguration<T> implements PrePersistListener<T>, PreUpdateListener<T>, PreRemoveListener<T> {

    protected static String getPrincipal() {
        return Optional.ofNullable(ContextProvider.getBean(ExtensionService.class)
                .getExtensionPoint(CurrentPrincipalProvider.class)).map(CurrentPrincipalProvider::getPrincipal)
            .orElse("system");
    }

    @Override
    public void preUpdate(EntityViewManager entityViewManager, EntityManager entityManager, Object view) {
        if (AuditableView.class.isAssignableFrom(view.getClass())) {
            AuditableView auditable = (AuditableView) view;
            auditable.setLastModifiedDate(new Date());
            auditable.setLastModifiedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
        }
    }

    @Override
    public void prePersist(EntityViewManager entityViewManager, EntityManager entityManager, Object view) {
        if (AuditableView.class.isAssignableFrom(view.getClass())) {
            AuditableView auditable = (AuditableView) view;
            Date date = new Date();
            auditable.setCreatedDate(date);
            auditable.setLastModifiedDate(date);
            auditable.setLastModifiedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
            auditable.setCreatedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
            auditable.setArchived(false);
        }
    }

    @Override
    public boolean preRemove(EntityViewManager entityViewManager, EntityManager entityManager, Object view) {
        if (AuditableView.class.isAssignableFrom(view.getClass())) {
            AuditableView auditable = (AuditableView) view;
            auditable.setLastModifiedDate(new Date());
            auditable.setLastModifiedBy(Objects.requireNonNullElse(getPrincipal(), "system"));
            auditable.setArchived(true);
        }
        return false;
    }
}
