package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
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
@EqualsAndHashCode(of = {"id", "name", "ngModuleName"})
public class ExposedModule implements Persistable<UUID>, Serializable, Comparable<ExposedModule> {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String name;

    private String ngModuleName;

    private String routePath;

    private String breadcrumb;

    private String title;

    private Integer position = 1;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private WebRemote webRemote;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "web_module_authorities",
        joinColumns = @JoinColumn(name = "module_id"))
    private Set<String> authorities = new HashSet<>();

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @Override
    public int compareTo(ExposedModule o) {
        return position.compareTo(o.position);
    }

    @EntityView(ExposedModule.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getName();

        String getNgModuleName();

        String getRoutePath();

        int getPosition();

        String getBreadcrumb();

        String getTitle();

        Set<String> getAuthorities();
    }
}
