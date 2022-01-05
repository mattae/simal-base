package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class WebComponent implements Serializable, Persistable<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String type;

    @NotNull
    private UUID componentId;

    @ManyToOne
    @NotNull
    private Module module;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "web_component_authorities",
        joinColumns = @JoinColumn(name = "component_id"))
    private Set<String> authorities = new HashSet<>();

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @EntityView(WebComponent.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getType();

        UUID getComponentId();

        Set<String> getAuthorities();
    }
}
