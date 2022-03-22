package com.mattae.simal.modules.base.yml;

import com.mattae.simal.modules.base.domain.entities.Menu;
import com.mattae.simal.modules.base.domain.entities.WebComponent;
import com.mattae.simal.modules.base.domain.entities.WebRemote;
import lombok.Data;

import java.util.*;

@Data
public class ModuleConfig {
    private String name;
    private String basePackage;
    private String version;
    private boolean store = true;
    private String description;
    private String author;
    private String email;
    private String url;
    private String image;
    private Date buildDate;
    private Configuration configuration;
    private List<Translation> translations = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    private List<WebRemote> webRemotes = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<Menu> menus = new ArrayList<>();
    private Set<WebComponent> webComponents = new HashSet<>();
}
