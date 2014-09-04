/*
 * Copyright 2014 ThoughtWorks (http://www.thoughtworks.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.thoughtworks.grails.plugin.cache

import grails.plugin.cache.ConfigLoader
import org.springframework.context.ApplicationContext

/**
 * Created by stevenlanders on 9/2/14.
 */
class FileSystemCacheConfigLoader extends ConfigLoader {

    void reload(List<ConfigObject> configs, ApplicationContext ctx) {

        FileSystemCacheConfigBuilder builder = new FileSystemCacheConfigBuilder()
        for (ConfigObject co : configs) {
            def config = co.config
            if (config instanceof Closure) {
                builder.parse config
            }
        }

        FileSystemCacheManager cacheManager = ctx.grailsCacheManager as FileSystemCacheManager

        for (String name in ([] + cacheManager.cacheNames)) {
            cacheManager.destroyCache name
        }

        def defaults = builder.getDefaults()

        if(defaults.containsKey("directory")){
            cacheManager.setDefaultDirectory(defaults.get("directory"))
        }

        builder.getCaches().each{cacheConfig->
            defaults.each{entry->
                if(!cacheConfig.containsKey(entry.getKey())){
                    cacheConfig.put(entry.getKey(), entry.getValue())
                }
            }
            cacheManager.addCache(cacheConfig["name"], cacheConfig["directory"])
        }

    }
}
