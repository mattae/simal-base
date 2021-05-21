package com.mattae.simal.modules.base.web.vm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class ScheduleResponse {
    private final boolean success;
    private final String message;
    private String jobId;
    private String jobGroup;
}
