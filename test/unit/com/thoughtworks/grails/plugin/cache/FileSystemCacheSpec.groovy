package com.thoughtworks.grails.plugin.cache

import grails.converters.JSON
import groovy.json.JsonSlurper
import spock.lang.Specification


/**
 * Created by stevenlanders on 9/3/14.
 */
class FileSystemCacheSpec extends Specification {

    static def TEMP_DIR = System.getProperty("java.io.tmpdir")
    static def CACHE_NAME = "FILE_SYSTEM_CACHE_SPEC_TEST_CACHE"

    void "test string cache"(){
        given:
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: TEMP_DIR
            )
        when:
            cache.put("entry", "value")
            def nativeCache = cache.getNativeCache()
        then:
            assert CACHE_NAME == cache.getName()
            assert nativeCache instanceof File
            assert nativeCache.exists()
            assert "value" == cache.get("entry").get()
            assert cacheEntryExists(CACHE_NAME,"567df20cb464db32e1e20d59160191f56e810a49c149701c37fdeffd931252c8.ser")
        cleanup:
            cache.clear()
    }

    void "test json cache"(){
        given:
            def jsonObj = JSON.parse('[{"val 1":"20.000000"}]')
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: TEMP_DIR
            )
        when:
            cache.put("json", jsonObj)
        then:
            assert jsonObj == cache.get("json").get()

    }

    void "test null cache"(){
        given:
        FileSystemCache cache = new FileSystemCache(
                name: CACHE_NAME,
                directory: TEMP_DIR
        )
        when:
        cache.put("nullobj", null)
        then:
        assert null == cache.get("nullobj")
    }

    void "test complicated key string cache"(){
        given:
        FileSystemCache cache = new FileSystemCache(
                name: CACHE_NAME,
                directory: TEMP_DIR
        )
        when:
        cache.put(new CacheableSerializableTestClass(name:"testobj"), "value")
        then:
        assert "value" == cache.get(new CacheableSerializableTestClass(name:"testobj")).get()
        cleanup:
        cache.clear()
    }

    void "test complicated key not serializable string cache"(){
        given:
        FileSystemCache cache = new FileSystemCache(
                name: CACHE_NAME,
                directory: TEMP_DIR
        )
        when:
        cache.put(new CacheableTestClass(name:"testobj"), "value")
        then:
        assert "value" == cache.get(new CacheableTestClass(name:"testobj")).get()
        cleanup:
        cache.clear()
    }

    void "test multiple string cache"(){
        given:
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: TEMP_DIR
            )
        when:
            cache.put("entry1", "value1")
            cache.put("entry2", "value2")

            def entry1 = cache.get("entry1").get()
            def entry2 = cache.get("entry2").get()
            cache.evict("entry1")

        then:
            assert "value1" == entry1
            assert "value2" == entry2
            assert null == cache.get("entry1")
        cleanup:
            cache.clear()
    }

    void "test serializable class"(){
        given:
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: TEMP_DIR
            )
        when:
            cache.put("person", new CacheableSerializableTestClass(name:"steven",phone:"800-555-1212"))
        then:
            assert "steven" == cache.get("person").get().name
            assert "800-555-1212" == cache.get("person").get().phone
            assert "steven" == cache.get("person",CacheableSerializableTestClass).name
            assert "800-555-1212" == cache.get("person",CacheableSerializableTestClass).phone
            assert cacheEntryExists(CACHE_NAME, "7e9c952e13b00bdae58213d728390edcdbebf7d3d05c6ffd1092b2f715aef911.ser")
        cleanup:
            cache.clear()
    }

    void "test basic map caching"(){
        given:
        FileSystemCache cache = new FileSystemCache(
                name: CACHE_NAME,
                directory: TEMP_DIR
        )
        when:
            cache.put("mydata",
                [
                        name:"steven",
                        phone:"800-555-1212",
                        favoriteColors: ["red","blue","green"]
                ]
            )
            def item = cache.get("mydata").get()
        then:
            assert "steven" == item.name
            assert "800-555-1212" == item.phone
            assert ["red","blue","green"] == item.favoriteColors
            assert cacheEntryExists(CACHE_NAME,"badda42d974df0743ec512bfb826181ca2492995ade72625d471b6bd2b5c313e.ser")
        cleanup:
            cache.clear()
    }

    void "test non-serializable class"(){
        given:
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: TEMP_DIR
            )
            println(TEMP_DIR)
        when:
            cache.put("person", new CacheableTestClass(name:"steven",phone:"800-555-1212"))
        then:
            assert "steven" == cache.get("person").get().name
            assert "800-555-1212" == cache.get("person").get().phone
            assert "steven" == cache.get("person",CacheableSerializableTestClass).name
            assert "800-555-1212" == cache.get("person",CacheableSerializableTestClass).phone
            assert cacheEntryExists(CACHE_NAME, "7e9c952e13b00bdae58213d728390edcdbebf7d3d05c6ffd1092b2f715aef911.ser")
         cleanup:
            cache.clear()
    }

    void "test string clear"(){
        given:
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: TEMP_DIR
            )
        when:
            cache.put("entry", "tobecleared")
            cache.clear()
        then:
            assert null == cache.get("entry")
            assert !cacheExists(CACHE_NAME)
    }

    void "test string evict"(){
        given:
        FileSystemCache cache = new FileSystemCache(
                name: CACHE_NAME,
                directory: TEMP_DIR
        )
        when:
            cache.put("entry", "tobeevicted")
            def result = cache.get("entry").get()
            cache.evict("entry")
        then:
            assert null == cache.get("entry")
            assert result == "tobeevicted"
            assert cacheExists(CACHE_NAME)
        cleanup:
            cache.clear()
    }

    void "test clear cache without directory illegal state"(){
        given:
            FileSystemCache cache = new FileSystemCache(
                    name: CACHE_NAME,
                    directory: null
            )
        when:
            cache.clear()
        then:
            thrown IllegalStateException
    }

    void "test clear cache without name illegal state"(){
        given:
        FileSystemCache cache = new FileSystemCache(
                name: null,
                directory: TEMP_DIR
        )
        when:
        cache.clear()
        then:
        thrown IllegalStateException
    }

    static boolean cacheExists(String cacheName){
        return new File("${TEMP_DIR}/${cacheName}/").exists()
    }

    static boolean cacheEntryExists(String cacheName, String filename){
        return new File("${TEMP_DIR}/${cacheName}/${filename}").exists()
    }

}

class CacheableSerializableTestClass implements Serializable{
    def name
    def phone
}

class CacheableTestClass {
    def name
    def phone
}
