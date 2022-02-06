package com.mattae.simal.modules.base.web.rest.vm;

import lombok.Data;

@Data
public class SearchVM {
    private String keyword;
    private Boolean active = true;
    private String type;
    private int start = 0;
    private int pageSize = 10;
}
