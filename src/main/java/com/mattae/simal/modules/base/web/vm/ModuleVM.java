package com.mattae.simal.modules.base.web.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ModuleVM {
    private UUID id;
    private String name;
    private String author;
    private String url;
    private String email;
    private Date buildDate;
    private String description;
    private String version;
    private boolean active;
    private boolean started;
    private Date buildTime;
    private String image;
    private String basePackage;
}
