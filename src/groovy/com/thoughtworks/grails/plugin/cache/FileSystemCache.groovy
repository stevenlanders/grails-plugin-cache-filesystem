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
import org.apache.commons.lang.SerializationUtils
import org.springframework.cache.Cache

import java.security.MessageDigest

/**
 * Created by stevenlanders on 9/2/14.
 */
class FileSystemCache implements Cache{

    String directory;
    String name;

    void writeObject(Object key, Object o){
        checkStatus()
        try {
            new File("${directory}/${name}/").mkdirs()
            def file = getFileByKey(key)
            file.write(JsonOutput.toJson(o))
        }catch(e){
            throw new RuntimeException(e)
        }
    }

    Object readObject(Object key){
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

    File getFileByKey(Object key){
        def filename = toFileName(key)
        def file = new File("${directory}/${name}/${filename}.json")
        return file;
    }

    static String toFileName(Object key){
        def md5Digest = MessageDigest.getInstance("SHA-256");
        md5Digest.reset();

        def serialKey = key
        if(!(serialKey instanceof Serializable)){
            serialKey = JsonOutput.toJson(key)
        }

        byte[] b = SerializationUtils.serialize((Serializable)serialKey)
        md5Digest.update(b);
        def digest = md5Digest.digest();
        return new BigInteger(1,digest).toString(16)
    }

    @Override
    String getName() {
        this.name
    }

    @Override
    Object getNativeCache() {
        checkStatus()
        return new File("${directory}/${name}")
    }

    @Override
    Cache.ValueWrapper get(Object key) {
        Object o = readObject(key)
        return (o == null) ? null : new GrailsValueWrapper(o, null)
    }

    @Override
    def <T> T get(Object key, Class<T> tClass) {
        return (T)readObject(key)
    }

    @Override
    void put(Object key, Object o) {
        writeObject(key, o)
    }

    @Override
    void evict(Object key) {
        def file = getFileByKey(key)
        if(file.exists()){
            file.delete()
        }
    }

    @Override
    void clear() {
        checkStatus()
        new File("${directory}/${name}").deleteDir()
    }

    private def checkStatus(){
        if(name == null){
            throw new IllegalStateException("FileSystemCaches must have name specified (name=${name})")
        }
        if(directory == null){
            throw new IllegalStateException("FileSystemCache ${name} must have directory specified (directory=${directory})")
        }
    }

}
