package com.mattae.simal.modules.base.domain.enumeration;

public enum PartyType {

    INDIVIDUAL {
        @Override
        public String toString() {
            return "INDIVIDUAL";
        }
    },
    ORGANISATION {
        @Override
        public String toString() {
            return "ORGANISATION";
        }
    },
    ORGANISATIONAL_UNIT {
        @Override
        public String toString() {
            return "ORGANISATIONAL_UNIT";
        }
    }
}
