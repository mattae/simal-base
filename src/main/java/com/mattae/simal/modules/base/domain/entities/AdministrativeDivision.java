package com.mattae.simal.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class AdministrativeDivision implements Serializable, Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String code;

    @ManyToOne
    @JsonIgnore
    private Country country;

    @ManyToOne
    @JsonIgnore
    private AdministrativeDivision parent;

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }
}
