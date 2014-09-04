grails-plugin-cache-filesystem
==============================

### Description

Filesystem implementation of the Grails cache plugin

This is for very simple caching scenarios where the filesystem is a reasonable place to cache results.  To clear the cache, delete the desired directory.

By default, cache files are created in Java's `-Djava.io.tmpdir` location  (where File.createTempFile creates its files)

### Usage

Include in your BuildConfig.groovy as follows:
`compile ':cache-filesystem:0.2'`

### Example (optional) configurations:

A cache config is actually not necessary unless you wish to specify a different directory than `-Djava-io-tmpdir`. 

To dyanmically add a cache, just use the annotation `@Cachable("cachename")`.  

Inside Config.groovy, use the following:
```groovy
//basic config
grails.cache.config = {
    cache {
        name 'cache2'
    }
}

//override directories
grails.cache.config = {
    cache {
        name 'cache1'
        directory '/my/special/cache/directory'
    }
    cache {
        name 'cache2'
    }
    defaults {
        directory '/tmp'
    }
}
```

