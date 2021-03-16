package org.lamisplus.modules.base.domain.entities;

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

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class WebComponent implements Serializable, Persistable<String> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @NotNull
    private String type;

    @NotNull
    private String componentId;

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
}
