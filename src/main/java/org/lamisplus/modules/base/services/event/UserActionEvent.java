package org.lamisplus.modules.base.services.event;

import lombok.Data;

@Data
public class UserActionEvent {
    private String login;
    private EventType type;

    public enum EventType {ADD, DELETE, UPDATE}
}
