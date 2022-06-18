package com.mattae.simal.modules.base.extensions;

public interface ExtensionPoint extends Comparable<ExtensionPoint> {
    default Integer getPriority() {
        return 1;
    }

    @Override
    default int compareTo(ExtensionPoint o) {
        int order = o.getPriority().compareTo(getPriority());
        if (order == 0) {
            order = getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
        }
        return order;
    }
}
