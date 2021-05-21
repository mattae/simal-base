package com.mattae.simal.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class IndividualName {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SequenceIndividualName")
    @SequenceGenerator(
        name = "SequenceIndividualName",
        allocationSize = 1
    )
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Individual individual;

    @Column(name = "type", nullable = false)
    private String type;

    @Embedded
    private Name name;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;
}
