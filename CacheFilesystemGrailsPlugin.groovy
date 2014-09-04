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
    // the plugin version
    def version = "0.4"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "web-app/**",
        "grails-app/views/**"
    ]

    def loadAfter = ['cache']

    def title = "Cache Filesystem Plugin" // Headline display name of the plugin
    def author = "Steven Landers"
    def authorEmail = "steven.landers@gmail.com"
    def description = '''\
A filesystem implementation of the Cache plugin
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/stevenlanders/grails-plugin-cache-filesystem"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "ThoughtWorks", url: "http://www.thoughtworks.com/" ]


    def doWithSpring = {

        grailsCacheManager(FileSystemCacheManager){
            defaultDirectory = System.getProperty("java.io.tmpdir")
        }

        grailsCacheConfigLoader(FileSystemCacheConfigLoader)

    }

}
