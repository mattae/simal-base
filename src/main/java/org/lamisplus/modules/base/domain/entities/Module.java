package org.lamisplus.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "module")
@EqualsAndHashCode(of = "name", callSuper = false)
@ToString(of = {"id", "name"})
@Slf4j
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

    private String version;

    private Date buildTime;

    @NotNull
    private Boolean active = true;

    private String artifact;

    private Boolean processConfig = true;

    private Boolean uninstall = false;

    private Boolean started = false;

    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    private byte[] data;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<WebRemote> webRemotes = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Form> forms = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<WebComponent> webComponents = new HashSet<>();

    @Override
    public boolean isNew() {
        return id == null;
    }

    public Module copy() {
        Module module = new Module();
        BeanUtils.copyProperties(this, module, "webComponents", "forms",
            "menus", "webRemotes");
        return module;
    }
}
