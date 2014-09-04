grails-plugin-cache-filesystem
==============================

### Description

Filesystem implementation of the [Grails cache plugin](http://grails.org/plugin/cache)

This is for very simple caching scenarios where the filesystem is a reasonable place to cache results.  To clear the cache, delete the desired directory or files.  Each object creates its own file (SHA256-encoded-key.json) in a directory specified in the configuration.

By default, cache files are created in Java's `-Djava.io.tmpdir` location  (where File.createTempFile creates its files)

### Usage

Include in your BuildConfig.groovy as follows:
`compile ':cache-filesystem:0.4'`

### Example (optional) configurations:

A cache config is actually not necessary unless you wish to specify a different directory than `-Djava-io-tmpdir`. 

To dynamically add a cache, just use the annotation `@Cachable("cachename")`.  For full `@Cacheable` usage, please see the official cache plugin's [documentation](http://grails-plugins.github.io/grails-cache/).  

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

### Behavior Details

Cached objects are cached in a location specified by */{directory}/{cachename}*

When evicted, cache files are deleted.  If `allentries=true` is used, all files are deleted.

The name of the object is the SHA-256 encoded serialized key (or JSON if not Serializable).json

#### Handling Space Limitations

Unless evicted via `@CacheEvict` files will continue to be added to the filesystem for each unique result.  The cache will grow indefinitely.  To recover space, one can simply delete files as desired.

Because this is a filesystem, it is possible to clean the cache directories per your needs via cronjob, or other external process.  For instance, you may choose to clean the cache every day, or only files older than some desired TTL. We can extend this plugin to offer auto-purging, if the need is voiced or preferred.  

There are some complexities to auto-purging which are elegantly solved by a cronjob:

```bash
#Delete files older than 5 days
find /cache/directory/* -mtime +5 -exec rm {} \;
```

#### A note on serialization:
Objects that are cached are converted to JSON then serialized to a file.  This initial conversion avoids cases where a basic Groovy object doesn't implement Serializable, but should still be reasonably writeable.  



