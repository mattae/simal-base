package org.lamisplus.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "form")
@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "name")
public class Form implements Serializable, Persistable<String> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @NotNull
    private String name;

    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode formData;

    private String componentId;

    @JsonIgnore
    private Integer priority = 1;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private Module module;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
