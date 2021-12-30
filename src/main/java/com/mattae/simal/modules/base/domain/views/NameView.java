package com.mattae.simal.modules.base.domain.views;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.UpdatableEntityView;
import com.mattae.simal.modules.base.domain.entities.Name;

@EntityView(Name.class)
@CreatableEntityView
@UpdatableEntityView
public interface NameView extends Name.View {

    void setTitle(String title);

    void setGivenName(String name);

    void setPreferredGivenName(String name);

    void setMiddleName(String name);

    void setInitials(String initials);

    void setFamilyName(String familyName);
}
