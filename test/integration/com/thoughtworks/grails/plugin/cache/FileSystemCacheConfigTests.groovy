package com.thoughtworks.grails.plugin.cache

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

/**
 * Created by stevenlanders on 9/2/14.
 */
@TestMixin(IntegrationTestMixin)
class FileSystemCacheConfigTests{

    def grailsApplication
    def grailsCacheManager
    def grailsCacheConfigLoader

    void testCanary(){
        assert true
    }

    void testGrailsCacheManagerInit(){
        grailsApplication.config.grails.cache.config = {
            defaults {
                directory '/tmp'
            }
        }

        grailsCacheConfigLoader.reload grailsApplication.mainContext

        def fileCacheManager = grailsCacheManager as FileSystemCacheManager
        assert "/tmp" == fileCacheManager.getDefaultDirectory()
    }

    void testGrailsConfigLoaded(){

        grailsApplication.config.grails.cache.config = {
            cache {
                name 'config1'
                directory '/my/test/directory'
            }
            defaults {
                directory '/tmp'
            }
        }

        grailsCacheConfigLoader.reload grailsApplication.mainContext
        assert grailsCacheManager.getCache("config1") instanceof FileSystemCache
        FileSystemCache cache = grailsCacheManager.getCache("config1") as FileSystemCache
        assert "config1" == cache.getName()
        assert "/my/test/directory" == cache.getDirectory()
    }

    void testGrailsDefaultDirectoryConfigLoaded(){

        grailsApplication.config.grails.cache.config = {
            cache {
                name 'defaultDirectoryConfig'
            }
            defaults {
                directory '/tmp'
            }
        }

        grailsCacheConfigLoader.reload grailsApplication.mainContext


        assert grailsCacheManager.getCache("defaultDirectoryConfig") instanceof FileSystemCache
        FileSystemCache cache = grailsCacheManager.getCache("defaultDirectoryConfig") as FileSystemCache
        assert "defaultDirectoryConfig" == cache.getName()
        assert "/tmp" == cache.getDirectory()
    }

    void testNoDefaultDirectorySpecified(){

        grailsApplication.config.grails.cache.config = {
            cache {
                name 'newconfig'
            }
        }

        grailsCacheConfigLoader.reload grailsApplication.mainContext

        //should be refreshed now
        assert !grailsCacheManager.cacheExists("config1")
        assert grailsCacheManager.cacheExists("newconfig")
        assert grailsCacheManager.getCache("newconfig") instanceof FileSystemCache
        FileSystemCache cache = grailsCacheManager.getCache("newconfig") as FileSystemCache
        assert "newconfig" == cache.getName()
        assert System.getProperty("java.io.tmpdir") == cache.getDirectory()

        def fileCacheManager = grailsCacheManager as FileSystemCacheManager
        assert System.getProperty("java.io.tmpdir") == fileCacheManager.getDefaultDirectory()

    }

}
