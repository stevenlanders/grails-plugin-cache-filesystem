package com.thoughtworks.grails.plugin.cache

import groovy.json.JsonOutput
import org.apache.commons.lang.SerializationUtils
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper

import java.security.MessageDigest

/**
 * Created by stevenlanders on 9/2/14.
 */
class FileSystemCache implements Cache{

    String directory;
    String name;

    void writeObject(Object key, Object o){
        checkStatus()
        def os = null
        try {
            new File("${directory}/${name}/").mkdirs()
            def file = getFileByKey(key)
            os = file.newObjectOutputStream()
            os << o
        }catch(NotSerializableException nse){
            throw new RuntimeException("Cacheable objects must implement Serializable", nse)
        }catch(e){
            throw new RuntimeException(e)
        }finally{
            os?.close()
        }
    }

    Object readObject(Object key){
        checkStatus()
        def os = null
        try{
            def file = getFileByKey(key)
            if(file.exists()){
                os = file.newObjectInputStream()
                return os.readObject()
            }
        }catch(e){
            throw new RuntimeException(e)
        }finally{
            os?.close()
        }
        return null
    }

    File getFileByKey(Object key){
        def filename = toFileName(key)
        def file = new File("${directory}/${name}/${filename}.ser")
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
        return (o == null) ? null : new SimpleValueWrapper(o)
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

    private static class SerialObjectWrapper implements Serializable{
        Object object

    }
}
