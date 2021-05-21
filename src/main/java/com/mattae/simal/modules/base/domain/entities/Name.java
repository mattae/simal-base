package com.mattae.simal.modules.base.domain.entities;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Name {

    private String title;

    private String givenName;

    private String preferredGivenName;

    private String middleName;

    private String initials;

    private String familyName;

    private String preferredFamilyName;

    private String preferredName;

    private String honorific;

    private String salutation;

}
