package com.mattae.simal.modules.base.domain.enumeration;

public enum LocationType {
    ADDRESS {
        @Override
        public String toString() {
            return "Address";
        }
    },
    ELECTRONIC_ADDRESS {
        @Override
        public String toString() {
            return "Electronic Address";
        }
    }

}
