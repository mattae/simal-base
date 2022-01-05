package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(of = {"id", "exposedName", "elementName", "componentName"})
public class ExposedComponent implements Persistable<UUID>, Serializable {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String exposedName;

    private String elementName;

    private String componentName;

    private String routePath;

    @NotNull
    private Integer position = 1;

    private String breadcrumb;

    private String title;

    private UUID name;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private WebRemote webRemote;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "component_authorities",
        joinColumns = @JoinColumn(name = "component_id"))
    private Set<String> authorities = new HashSet<>();

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @EntityView(ExposedComponent.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getExposedName();

        String getElementName();

        String getComponentName();

        String getRoutePath();

        int getPosition();

        String getBreadcrumb();

        String getTitle();

        UUID getName();

        @Mapping("webRemote.remoteEntry")
        String getRemoteEntry();

        @Mapping("webRemote.remoteName")
        String getRemoteName();

        Set<String> getAuthorities();
    }
}
