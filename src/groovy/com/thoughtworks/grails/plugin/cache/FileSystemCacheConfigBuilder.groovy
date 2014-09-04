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

import grails.plugin.cache.ConfigBuilder

/**
 * Created by stevenlanders on 9/3/14.
 */
class FileSystemCacheConfigBuilder extends ConfigBuilder {

    def defaults = [
            directory : System.getProperty("java.io.tmpdir")
    ]

    protected static final List CACHE_PARAM_NAMES = [
            'directory','name','env'
    ]

    public def getCaches(){
        return _caches
    }

    @Override
    protected createNode(name) {
        if (_unrecognizedElementDepth) {
            _unrecognizedElementDepth++
            _log.warn "ignoring node $name contained in unrecognized parent node"
            return
        }

        _log.trace "createNode $name"

        switch (name) {
            case 'cache':
            case 'defaults':
            case 'domain':
                _current = [:]
                _caches << _current
                _stack.push name
                return name
        }

        _unrecognizedElementDepth++
        _log.warn "Cannot create empty node with name '$name'"
    }

    //overridden to allow different list of CACHE_PARAM_NAMES
    @Override
    protected createNode(name, value) {

        if (_unrecognizedElementDepth) {
            _unrecognizedElementDepth++
            _log.warn "ignoring node $name with value $value contained in unrecognized parent node"
            return
        }

        _log.trace "createNode $name, value: $value"

        String level = _stack[-1]
        _stack.push name

        switch (level) {

            case 'domain':
            case 'defaults' :

                if (('name' == name || 'cache' == name || 'domain' == name) && value instanceof Class) {
                    value = value.name
                }

                if ('name' == name || 'cache' == name  || 'domain' == name || name in CACHE_PARAM_NAMES) {
                    defaults[name] = value
                    return name
                }

                break
            case 'cache':
                if (('name' == name || 'cache' == name || 'domain' == name) && value instanceof Class) {
                    value = value.name
                }

                if ('name' == name || 'cache' == name  || 'domain' == name || name in CACHE_PARAM_NAMES) {
                    _current[name] = value
                    return name
                }

                break
        }

        _unrecognizedElementDepth++
        _log.warn "Cannot create node with name '$name' and value '$value' for parent '$level'"
    }


}
