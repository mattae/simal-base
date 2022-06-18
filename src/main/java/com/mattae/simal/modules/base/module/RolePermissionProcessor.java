package com.mattae.simal.modules.base.module;

import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.extensions.ExtensionPoint;
import com.mattae.simal.modules.base.yml.ModuleConfig;

public interface RolePermissionProcessor extends ExtensionPoint {
    void saveRolesAndPermissions(Module module, ModuleConfig moduleConfig);

    void deleteRolesAndPermissions(Module module);
}
