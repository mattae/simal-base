package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

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

    @ManyToOne
    private Party party;
}
