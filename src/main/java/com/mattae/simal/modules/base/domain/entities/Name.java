package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
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

    @EntityView(Name.class)
    public interface View {
        String getTitle();

        @NotBlank
        String getGivenName();

        String getPreferredGivenName();

        String getMiddleName();

        String getInitials();

        @NotBlank
        String getFamilyName();
    }
}
