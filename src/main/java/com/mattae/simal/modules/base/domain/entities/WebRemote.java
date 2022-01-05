package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "web_remote")
@EqualsAndHashCode(of = {"id", "remoteName"})
@ToString(of = {"id", "remoteName", "remoteEntry"})
public final class WebRemote implements Persistable<UUID>, Serializable {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String remoteName;

    @NotNull
    private String remoteEntry;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private Module module;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "webRemote", cascade = CascadeType.ALL)
    Set<ExposedModule> modules = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "webRemote", cascade = CascadeType.ALL)
    Set<ExposedComponent> components = new HashSet<>();

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @EntityView(WebRemote.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getRemoteName();

        String getRemoteEntry();

        Set<ExposedModule.View> getModules();

        Set<ExposedComponent.View> getComponents();
    }
}
