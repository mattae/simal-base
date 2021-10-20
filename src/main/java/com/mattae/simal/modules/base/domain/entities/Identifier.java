package com.mattae.simal.modules.base.domain.entities;

import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLDelete(sql = "update identifier set archived = true, last_modified_date = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
public class Identifier {
    @Id
    @GeneratedValue
    private UUID id;

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

    @ManyToOne
    private Party party;

    @PreUpdate
    public void update() {
        lastModifiedDate = LocalDateTime.now();
    }
}
