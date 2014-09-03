import com.thoughtworks.grails.plugin.cache.FileSystemCacheConfigLoader
import com.thoughtworks.grails.plugin.cache.FileSystemCacheManager

class CacheFilesystemGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def loadAfter = ['cache']

    // TODO Fill in these fields
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

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {

        grailsCacheManager(FileSystemCacheManager){
            defaultDirectory = System.getProperty("java.io.tmpdir")
        }

        grailsCacheConfigLoader(FileSystemCacheConfigLoader)

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
