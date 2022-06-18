package com.mattae.simal.modules.base.config;

import com.mattae.simal.modules.base.extensions.ExtensionPoint;

public interface CurrentPrincipalProvider extends ExtensionPoint {
    String getPrincipal();
}
