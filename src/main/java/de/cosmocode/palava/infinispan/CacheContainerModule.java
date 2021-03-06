/**
 * Copyright 2010 CosmoCode GmbH
 *
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

package de.cosmocode.palava.infinispan;

import java.io.File;
import java.lang.annotation.Annotation;

import org.infinispan.manager.CacheContainer;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.inject.Config;

/**
 * Binds {@link CacheContainer} to {@link ConfigurableCacheContainer}.
 *
 * @since 1.1
 * @author Willi Schoenborn
 */
public final class CacheContainerModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(CacheContainer.class).to(ConfigurableCacheContainer.class).in(Singleton.class);
    }
    
    /**
     * Rebinds configuration using the specified annotation and name.
     * 
     * @since 1.1
     * @param annotation the binding annotation
     * @param name the config prefix
     * @return a module used for rebinding
     */
    public static Module annotatedWith(Class<? extends Annotation> annotation, String name) {
        return new AnnotationModule(annotation, name);
    }
    
    /**
     * Rebind module for {@link CacheContainer}s.
     *
     * @since 1.1
     * @author Willi Schoenborn
     */
    private static final class AnnotationModule extends PrivateModule {
        
        private final Class<? extends Annotation> annotation;
        private final Config config;
        
        public AnnotationModule(Class<? extends Annotation> annotation, String name) {
            this.annotation = Preconditions.checkNotNull(annotation);
            this.config = new Config(name);
        }
        
        @Override
        protected void configure() {
            bind(File.class).annotatedWith(Names.named(InfinispanConfig.CONFIG)).to(
                Key.get(File.class, Names.named(config.prefixed(InfinispanConfig.CONFIG))));
            
            bind(CacheContainer.class).annotatedWith(annotation).
                to(ConfigurableCacheContainer.class).in(Singleton.class);
            
            expose(CacheContainer.class).annotatedWith(annotation);
        }
        
    }

}
