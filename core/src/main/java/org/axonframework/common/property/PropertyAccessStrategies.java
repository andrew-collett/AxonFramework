package org.axonframework.common.property;

import org.axonframework.serviceregistry.ServiceRegistry;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

class PropertyAccessStrategies {

    public static final SortedSet<PropertyAccessStrategy> STRATEGIES = new ConcurrentSkipListSet<>();

    static {
        for (PropertyAccessStrategy factory : ServiceRegistry.get().getPropertyAccessStrategies()) {
            STRATEGIES.add(factory);
        }
    }

}
