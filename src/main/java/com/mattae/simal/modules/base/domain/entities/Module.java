package com.mattae.simal.modules.base.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foreach.across.modules.filemanager.business.reference.FileReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "module")
@EqualsAndHashCode(of = "name", callSuper = false)
public class Module implements Serializable, Persistable<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    @Column(unique = true)
    private String basePackage;

    private String version;

    private Date buildTime;

    @NotNull
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    @JsonIgnore
    private FileReference file;

    private Boolean processConfig = true;

    private Boolean uninstall = false;

    private Boolean started = false;

    private String virtualPath;

    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    private byte[] data;

    @Override
    public boolean isNew() {
        return id == null;
    }

    public Module copy() {
        Module module = new Module();
        BeanUtils.copyProperties(this, module, "file", "data");
        return module;
    }

    @EntityView(Module.class)
    public interface View {
        @IdMapping
        UUID getId();

        String getName();

        String getBasePackage();

        String getVersion();

        Date getBuildTime();

        Boolean getActive();
    }
}
