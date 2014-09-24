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

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

/**
 * @author stevenlanders
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

        def fileCacheManager = grailsCacheManager
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
        FileSystemCache cache = grailsCacheManager.getCache("config1")
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
        FileSystemCache cache = grailsCacheManager.getCache("defaultDirectoryConfig")
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
        FileSystemCache cache = grailsCacheManager.getCache("newconfig")
        assert "newconfig" == cache.getName()
        assert System.getProperty("java.io.tmpdir") == cache.getDirectory()

        def fileCacheManager = grailsCacheManager
        assert System.getProperty("java.io.tmpdir") == fileCacheManager.getDefaultDirectory()
    }
}
