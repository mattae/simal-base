package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Address {
    @Id
    @GeneratedValue
    private UUID id;

    private String line1;

    private String line2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String addressType;
}
