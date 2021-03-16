package org.lamisplus.modules.base.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ComponentDTO {
    private String remoteName;
    private String componentName;
    private String elementName;
    private String exposedName;
    @JsonIgnore
    private Set<String> authorities = new HashSet<>();
}
