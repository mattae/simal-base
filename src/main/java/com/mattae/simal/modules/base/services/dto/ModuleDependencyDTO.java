package com.mattae.simal.modules.base.services.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ModuleDependencyDTO {
    private UUID id;
    private String name;
    private Boolean active;
    private String requiredVersion;
    private String installedVersion;
    private Boolean versionSatisfied;
}
