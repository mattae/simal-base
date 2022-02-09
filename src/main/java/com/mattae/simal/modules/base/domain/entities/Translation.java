package com.mattae.simal.modules.base.domain.entities;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Module module;

    @EqualsAndHashCode.Include
    @NotNull
    private String lang;

    @Column(name = "_order")
    private Integer order = 1;

    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    @NotNull
    private JsonNode data;
}
