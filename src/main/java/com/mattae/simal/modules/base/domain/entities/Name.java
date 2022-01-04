package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@Data
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

        String getGivenName();

        String getPreferredGivenName();

        String getMiddleName();

        String getInitials();

        String getFamilyName();
    }
}
