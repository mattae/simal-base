package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Identifier {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SequenceIdentifier")
    @SequenceGenerator(
        name = "SequenceIdentifier",
        allocationSize = 1
    )
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "register", nullable = false)
    private String register;

    private String lifecycleStatus;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;
}
