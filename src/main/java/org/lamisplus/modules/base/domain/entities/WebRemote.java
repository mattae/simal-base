package org.lamisplus.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "web_remote")
@EqualsAndHashCode(of = {"id", "remoteName"})
public final class WebRemote implements Persistable<Long>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
