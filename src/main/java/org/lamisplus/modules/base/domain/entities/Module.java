package org.lamisplus.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "module")
@EqualsAndHashCode(of = "name", callSuper = false)
@ToString(of = {"id", "name"})
public class Module implements Serializable, Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    @Column(unique = true)
    private String basePackage;

    private String description;

    //@Pattern(regexp = "\\d+\\..*")
    private String version;

    private ZonedDateTime buildTime;

    @NotNull
    private Boolean active = true;

    private String artifact;

    private String umdLocation;

    private String moduleMap;

    private Boolean inError;

    private Boolean installOnBoot;

    private Integer priority = 100;

    @OneToOne(mappedBy = "module", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private ModuleArtifact moduleArtifact;

    @OneToMany(mappedBy = "module", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Set<WebModule> webModules = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JsonIgnore
    private Set<Authority> bundledAuthorities = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = {CascadeType.ALL})
    @JsonIgnore
    private Set<ModuleDependency> dependencies = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JsonIgnore
    private Set<Form> templates = new HashSet<>();

    @Override
    public boolean isNew() {
        return id == null;
    }
}
