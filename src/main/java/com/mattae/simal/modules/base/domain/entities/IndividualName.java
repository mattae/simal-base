package com.mattae.simal.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class IndividualName {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Individual individual;

    @Column(name = "type", nullable = false)
    private String type;

    @Embedded
    private Name name;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;
}
