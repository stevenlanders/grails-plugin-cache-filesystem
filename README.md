grails-plugin-cache-filesystem
==============================

Filesystem implementation of the Grails cache plugin

This is for very simple caching scenarios where the filesystem is a reasonable place to cache results.  To clear the cache, delete the desired directory.

By default, cache files are created in Java's System.getProperty("java.io.tmpdir") location  (where File.createTempFile creates its files)

### Example (optional) configurations:

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

If desired, one can choose to not specify a cache config, and just use @Cachable("cachename").  In this case, default temp directory is used.
