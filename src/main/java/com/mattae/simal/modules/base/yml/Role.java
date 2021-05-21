package com.mattae.simal.modules.base.yml;

import lombok.Data;

import java.util.Set;

@Data
public class Role {
    Set<Permission> permissions;
    private String name;
    private String authority;
    private String description;
}
