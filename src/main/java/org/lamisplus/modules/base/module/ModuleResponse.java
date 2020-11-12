package org.lamisplus.modules.base.module;

import lombok.Data;
import org.lamisplus.modules.base.domain.entities.Module;

@Data
public class ModuleResponse {
    public enum Type {ERROR, SUCCESS}

    private Type type;
    private String message;
    private Module module;
}
