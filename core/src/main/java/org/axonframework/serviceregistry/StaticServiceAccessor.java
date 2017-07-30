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

import org.axonframework.commandhandling.model.inspection.ChildEntityDefinition;
import org.axonframework.common.property.PropertyAccessStrategy;
import org.axonframework.messaging.annotation.HandlerDefinition;
import org.axonframework.messaging.annotation.HandlerEnhancerDefinition;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.serialization.ContentTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * An implementation of ServiceAccessor that caches all Service implementations on initialisation
 */
public class StaticServiceAccessor implements ServiceAccessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(StaticServiceAccessor.class);

    private List<ChildEntityDefinition> childEntityDefinitions = new ArrayList<>();
    private List<HandlerDefinition> handlerDefinitions = new ArrayList<>();
    private List<HandlerEnhancerDefinition> handlerEnhancerDefinitions = new ArrayList<>();
    private List<ParameterResolverFactory> parameterResolverFactories = new ArrayList<>();

    private static boolean initialized = false;

    public StaticServiceAccessor()  {

        LOGGER.info("Initializing StaticServiceAccessor...");
        final ClassLoader myClassLoader = StaticServiceAccessor.class.getClassLoader();
        final ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(myClassLoader);
            ServiceLoader.load(ChildEntityDefinition.class).forEach(childEntityDefinitions::add);
            LOGGER.debug("Registered ChildEntityDefinitions:");
            childEntityDefinitions.forEach(def -> LOGGER.info("{}", def.getClass().getSimpleName()));
            ServiceLoader.load(HandlerDefinition.class).forEach(handlerDefinitions::add);
            LOGGER.debug("Registered HandlerDefinitions:");
            handlerDefinitions.forEach(def -> LOGGER.info("{}", def.getClass().getSimpleName()));
            ServiceLoader.load(HandlerEnhancerDefinition.class).forEach(handlerEnhancerDefinitions::add);
            LOGGER.debug("Registered HandlerEnhancerDefinitions:");
            handlerEnhancerDefinitions.forEach(def -> LOGGER.info("{}", def.getClass().getSimpleName()));
            ServiceLoader.load(ParameterResolverFactory.class).forEach(parameterResolverFactories::add);
            LOGGER.debug("Registered ParameterResolverFactories:");
            parameterResolverFactories.forEach(def -> LOGGER.info("{}", def.getClass().getSimpleName()));
            initialized = true;
        }
        catch (Exception ex) {
            LOGGER.error("Failed to initialize SspAxonServiceRegistry: {}", ex.getMessage());
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
    }


    @Override
    public List<ChildEntityDefinition> getChildEntityDefinitions() {
        return Collections.unmodifiableList(childEntityDefinitions);
    }

    @Override
    public List<PropertyAccessStrategy> getPropertyAccessStrategies() {
        return null;
    }

    @Override
    public List<HandlerDefinition> getHandlerDefinitions() {
        return Collections.unmodifiableList(handlerDefinitions);
    }

    @Override
    public List<HandlerEnhancerDefinition> getHandlerEnhancerDefinitions() {
        return Collections.unmodifiableList(handlerEnhancerDefinitions);
    }

    @Override
    public List<ParameterResolverFactory> getParameterResolverFactories() {
        return Collections.unmodifiableList(parameterResolverFactories);
    }

    @Override
    public List<ContentTypeConverter> getContentTypeConverters() {
        return null;
    }
}
