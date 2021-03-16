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
@EqualsAndHashCode(of = {"id", "exposedName", "elementName", "componentName"})
public class ExposedComponent implements Persistable<Long>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String exposedName;

    private String elementName;

    private String componentName;

    @NotNull
    private String routePath;

    @NotNull
    private Integer position = 1;

    private String breadcrumb;

    private String title;

    private String uuid;

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
}
