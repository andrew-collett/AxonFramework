/*
 * Copyright (c) 2010-2017. Axon Framework
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.serviceregistry;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Implement a static registry that can be used to obtain a ServiceAccessor
 * from anywhere in the code-base
 * Custom implementations of ServiceAccessor can be injected into this Registry
 */
public class ServiceRegistry {

    private static AtomicReference<ServiceAccessor> accessor = new AtomicReference<>();

    static {
        // Set default ServiceAccessor to StaticServiceAccessor
        accessor.set(new StaticServiceAccessor());
    }

    public static void setAccessor(ServiceAccessor newAccessor) {
        if (newAccessor != null) {
            accessor.compareAndSet(accessor.get(), newAccessor);
        }
    }

    public static ServiceAccessor get() {
        return accessor.get();
    }

}
