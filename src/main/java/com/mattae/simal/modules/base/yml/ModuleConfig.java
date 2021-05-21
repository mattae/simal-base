package com.mattae.simal.modules.base.yml;

import com.mattae.simal.modules.base.domain.entities.Menu;
import lombok.Data;
import com.mattae.simal.modules.base.domain.entities.WebComponent;
import com.mattae.simal.modules.base.domain.entities.WebRemote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ModuleConfig {
    private String name;
    private String basePackage;
    private String version;
    private boolean store = true;
    private String summary;
    private List<Dependency> dependencies = new ArrayList<>();
    private List<WebRemote> webRemotes = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<Menu> menus = new ArrayList<>();
    private Set<WebComponent> webComponents = new HashSet<>();
}
