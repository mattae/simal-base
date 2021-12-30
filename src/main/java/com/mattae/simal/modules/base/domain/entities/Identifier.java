package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Where(clause = "archived = false")
@Data
public class Identifier {

    @Column(name = "type", nullable = false)
    @EqualsAndHashCode.Include
    private String type;

    @Column(name = "value", nullable = false)
    @EqualsAndHashCode.Include
    private String value;

    @Column(name = "register", nullable = false)
    @EqualsAndHashCode.Include
    private String register;

    private String lifecycleStatus;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private Boolean archived = false;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }

    @EntityView(Identifier.class)
    public interface View {

        String getType();

        String getValue();

        String getRegister();

        LocalDateTime getFromDate();

        LocalDateTime getToDate();
    }
}
