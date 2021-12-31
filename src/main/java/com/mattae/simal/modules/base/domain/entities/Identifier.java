package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "party_identifiers")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Where(clause = "archived = false")
@Getter
@Setter
@SQLDelete(sql = "update party_identifiers set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
public class Identifier {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    UUID id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Party party;

    private Boolean archived = false;

    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }

    @EntityView(Identifier.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getType();

        String getValue();

        String getRegister();

        LocalDateTime getFromDate();

        LocalDateTime getToDate();
    }
}
