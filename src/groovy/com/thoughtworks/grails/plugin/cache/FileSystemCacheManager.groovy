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

import grails.plugin.cache.GrailsCacheManager
import org.springframework.cache.Cache

/**
 * @author stevenlanders
 */
class FileSystemCacheManager implements GrailsCacheManager {

    def cacheMap = [:]
    String defaultDirectory

    void addCache(String name, String directory) {
        if (directory == null) {
            directory = defaultDirectory
        }
        cacheMap.put(name, new FileSystemCache(name: name, directory: directory))
    }

    boolean cacheExists(String name) {
        return name == null ? false : cacheMap.containsKey(name)
    }

    boolean destroyCache(String name) {
        if(cacheExists(name)){
            getCache(name).clear()
        }
        cacheMap.remove(name)
    }

    Cache getCache(String name) {
        if(!cacheExists(name)){
            cacheMap.put(name, new FileSystemCache(name: name, directory: defaultDirectory))
        }
        return (Cache)cacheMap.get(name)
    }

    Collection<String> getCacheNames() {
        return cacheMap.keySet()
    }
}

