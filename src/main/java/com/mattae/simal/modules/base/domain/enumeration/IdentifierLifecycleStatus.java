package com.mattae.simal.modules.base.domain.enumeration;

import lombok.Getter;

@Getter
public enum IdentifierLifecycleStatus {

    ACTIVE(1) {
        @Override
        public String toString() {
            return "Active";
        }
    },
    SUSPENDED(2) {
        @Override
        public String toString() {
            return "Suspended";
        }
    },
    CANCELLED(4) {
        @Override
        public String toString() {
            return "Cancelled";
        }
    };

    private final Integer code;

    IdentifierLifecycleStatus(Integer code) {
        this.code = code;
    }

}
