package org.lamisplus.modules.base.services.dto;

import com.foreach.across.modules.user.business.Role;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
@Slf4j
public class RoleDTO {
    private Long id;
    private String name;
    private String authority;
    private String description;

    private Set<PermissionDTO> permissions;

    public Role role() {
        Role role = new Role();
        BeanUtils.copyProperties(this, role, "permissions");
        return role;
    }
}
