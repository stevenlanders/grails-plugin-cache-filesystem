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

import com.thoughtworks.grails.plugin.cache.FileSystemCacheConfigLoader
import com.thoughtworks.grails.plugin.cache.FileSystemCacheManager

class CacheFilesystemGrailsPlugin {
    def version = "0.4"
    def grailsVersion = "2.4 > *"
    def pluginExcludes = [
        "web-app/**",
        "grails-app/views/**"
    ]

    def loadAfter = ['cache']

    def title = "Cache Filesystem Plugin"
    def author = "Steven Landers"
    def authorEmail = "steven.landers@gmail.com"
    def description = 'A filesystem implementation of the Cache plugin'
    def documentation = "https://github.com/stevenlanders/grails-plugin-cache-filesystem"
    def license = "APACHE"
    def organization = [ name: "ThoughtWorks", url: "http://www.thoughtworks.com/" ]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/stevenlanders/grails-plugin-cache-filesystem/issues']
    def scm = [url: 'https://github.com/stevenlanders/grails-plugin-cache-filesystem']

    def doWithSpring = {

        grailsCacheManager(FileSystemCacheManager){
            defaultDirectory = System.getProperty("java.io.tmpdir")
        }

        grailsCacheConfigLoader(FileSystemCacheConfigLoader)
    }
}
