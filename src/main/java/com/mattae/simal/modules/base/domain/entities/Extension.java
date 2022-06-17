package com.mattae.simal.modules.base.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "extension_point")
public class Extension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String name;

    private String interfaceName;

    private String extensionText;

    private ExtensionType extensionType;

    private Boolean enabled = true;

    public enum ExtensionType {
        JAVA, GROOVY, BSH
    }
}
