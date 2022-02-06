package com.mattae.simal.modules.base.domain.views;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public interface AuditableView {
    @JsonIgnore
    Date getLastModifiedDate();

    void setLastModifiedDate(Date date);

    @JsonIgnore
    Date getCreatedDate();

    void setCreatedDate(Date date);

    @JsonIgnore
    String getCreatedBy();

    void setCreatedBy(String createdBy);

    @JsonIgnore
    String getLastModifiedBy();

    void setLastModifiedBy(String lastModifiedBy);

    @JsonIgnore
    Boolean getArchived();

    void setArchived(Boolean archived);
}
