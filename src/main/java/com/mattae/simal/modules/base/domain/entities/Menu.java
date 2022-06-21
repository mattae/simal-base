package com.mattae.simal.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import com.mattae.simal.modules.base.domain.enumeration.MenuLevel;
import com.mattae.simal.modules.base.domain.enumeration.MenuType;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "menu")
@EqualsAndHashCode(of = {"name", "level", "state"})
@ToString(of = {"id", "name", "state", "type", "subs", "level", "module", "position"}, callSuper = true)
@Slf4j
public final class Menu implements Serializable, Comparable<Menu>, Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    private String name;

    private String state;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MenuType type = MenuType.link;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private MenuLevel level = MenuLevel.LEVEL_1;

    @NotNull
    private Integer position = 1;

    private String icon;

    private String tooltip;

    private Boolean modal = false;

    private Boolean disabled = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "menu_authorities",
        joinColumns = @JoinColumn(name = "menu_id"))
    private Set<String> authorities = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotNull
    private Module module;

    @ManyToOne
    @JsonIgnore
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Menu> subs = new TreeSet<>();

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @Override
    public int compareTo(Menu o) {
        if (position.equals(o.position)) {
            return name.compareTo(o.name);
        }
        return position.compareTo(o.position);
    }

    @PostLoad
    public void load() {
    }

    @PrePersist
    @PreUpdate
    public void update() {
        if (parent != null) {
            level = MenuLevel.LEVEL_2;
            if (parent.parent != null) {
                level = MenuLevel.LEVEL_3;
            }
        } else {
            level = MenuLevel.LEVEL_1;
        }
        if (!subs.isEmpty()) {
            type = MenuType.dropDown;
        }
    }
}
