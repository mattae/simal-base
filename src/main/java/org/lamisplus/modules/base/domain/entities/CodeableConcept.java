package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class CodeableConcept implements Serializable, Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "concept_codifier",
            joinColumns = @JoinColumn(name = "concept_id"),
            inverseJoinColumns = @JoinColumn(name = "codifier_id"))
    private Collection<Codifier> codifiers;

    @NotNull
    private String text;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
