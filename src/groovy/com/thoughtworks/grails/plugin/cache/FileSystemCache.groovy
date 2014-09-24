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

import grails.plugin.cache.GrailsValueWrapper
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.security.MessageDigest

import org.apache.commons.lang.SerializationUtils
import org.springframework.cache.Cache
import org.springframework.util.Assert

/**
 * @author stevenlanders
 */
class FileSystemCache implements Cache {

    String directory
    String name

    void writeObject(key, o) {
        checkStatus()
        try {
            new File(directory, name).mkdirs()
            def file = getFileByKey(key)
            file.write(JsonOutput.toJson(o))
        }catch(e){
            throw new RuntimeException(e)
        }
    }

    Object readObject(key) {
        checkStatus()
        try{
            def file = getFileByKey(key)
            if(file.exists()){
                return new JsonSlurper().parse(file)
            }
        }catch(e){
            throw new RuntimeException(e)
        }
        return null
    }

    File getFileByKey(key) {
        def filename = toFileName(key)
        def file = new File("${directory}/${name}/${filename}.json")
        return file
    }

    static String toFileName(key) {
        def md5Digest = MessageDigest.getInstance("SHA-256")
        md5Digest.reset()

        def serialKey = key
        if(!(serialKey instanceof Serializable)){
            serialKey = JsonOutput.toJson(key)
        }

        byte[] b = SerializationUtils.serialize(serialKey)
        md5Digest.update(b)
        def digest = md5Digest.digest()
        return new BigInteger(1,digest).toString(16)
    }

    Object getNativeCache() {
        checkStatus()
        return new File(directory, name)
    }

    Cache.ValueWrapper get(key) {
        Object o = readObject(key)
        return (o == null) ? null : new GrailsValueWrapper(o, null)
    }

    def <T> T get(key, Class<T> tClass) {
        return readObject(key)
    }

    void put(key, o) {
        writeObject(key, o)
    }

    void evict(key) {
        def file = getFileByKey(key)
        if(file.exists()){
            file.delete()
        }
    }

    void clear() {
        checkStatus()
        new File(directory, name).deleteDir()
    }

    private void checkStatus() {
        Assert.state(name != null, "FileSystemCaches must have name specified (name=${name})")
        Assert.state(directory != null, "FileSystemCache ${name} must have directory specified (directory=${directory})")
    }
}
