package com.thoughtworks.grails.plugin.cache

import grails.plugin.cache.GrailsCacheManager
import org.springframework.cache.Cache

/**
 * Created by stevenlanders on 9/2/14.
 */
class FileSystemCacheManager implements GrailsCacheManager {

    def cacheMap = [:]
    def defaultDirectory;

    void addCache(def name, def directory){
        if(directory == null){
            directory = defaultDirectory
        }
        cacheMap.put(name, new FileSystemCache(name: name, directory: directory))
    }

    @Override
    boolean cacheExists(String name) {
        return name == null ? false : cacheMap.containsKey(name)
    }

    @Override
    boolean destroyCache(String name) {
        if(cacheExists(name)){
            getCache(name).clear()
        }
        cacheMap.remove(name)
    }

    @Override
    Cache getCache(String name) {
        if(!cacheExists(name)){
            cacheMap.put(name, new FileSystemCache(name: name, directory: defaultDirectory))
        }
        return (Cache)cacheMap.get(name)
    }

    @Override
    Collection<String> getCacheNames() {
        return cacheMap.keySet()
    }
}

