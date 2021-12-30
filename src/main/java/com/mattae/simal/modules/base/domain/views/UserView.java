package com.mattae.simal.modules.base.domain.views;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.foreach.across.modules.user.business.User;

@EntityView(User.class)
public interface UserView {
    @IdMapping
    Long getId();
}
