import edu.usf.CASRestClient.*
class CASRestClientGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [rest:'0.7 > *']
    // def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "CAS Rest Client Plugin" // Headline display name of the plugin
    def author = "James Jones"
    def authorEmail = "james@mail.usf.edu"
    def description = '''\
The CAS Rest Client Plugin provides a mechanism to execute REST based services protected by CAS and CAS Spring Security.

Usage
-----

The plugin will inject the following dynamic methods in both services and controllers:

 * `withCASRest(String serviceUrl,String method = "GET",def query=[:])` - executes a service wrapped in CAS using the REST plugin
 * `withCASSpringSecurityRest(String serviceUrl,String method = "GET",def query=[:],String springSecurityBaseUrl)` - executes a service wrapped in CAS Spring Security using the REST plugin
 
The properties are described as:

 * serviceUrl - The full URL used for the service
 * method - The HTTP method to use (GET,PUT,DELETE,POST). The default is "GET"
 * query - A hashmap of key/value pairs to be passed to the service
 * springSecurityBaseUrl - The base URL of your app that is using Spring Security (not the full service URL, this is used to get the JSESSIONID for spring security)

Configuration
-------------

By default, a `CASRestClientConfig.groovy` file is added to your configuration directory. 

You can simply `add` this to your Config.groovy config locations with:

grails.config.locations = [ "file:${basedir}/grails-app/conf/CASRestClientConfig.groovy" ]

... or you can externalize this like any other config file

You'll need to edit this configuration file with the correct CAS server URL, tickets path and valid credentials that can run your target services authenticated by CAS 
and authorized by Spring Security or some other authorization method 

Example
-------

String result = withCasRest("https://dev.it.usf.edu/nams/ws_convert","GET",[submit_type:"netid",return_type:"mail","return":"json",value:"james"])


The result will be as a string: {"response":"success","netid":"james","mail":"james@usf.edu"}

You will have to do any type conversion to JSON,XML,etc as the output is a raw string.class.

'''

    // URL to the plugin's documentation
    def documentation = "http://bullpen.jira.com/wiki"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "USF", url: "http://www.usf.edu/" ]

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
        // TODO Implement runtime spring config (optional)
        cASRestServiceCallService(edu.usf.CASRestClient.CASRestServiceCallService)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
        // def cASRestServiceCallService = application.getServiceClass("CASRestServiceCallService").newInstance()
        def cASRestServiceCallService = ctx.getBean('cASRestServiceCallService')
        for (serviceClass in application.serviceClasses) {
             serviceClass.metaClass.withCASRest = { String serviceUrl,String method = "GET",def query=[:] ->
                return cASRestServiceCallService.executeCASService(serviceUrl,method,query)
             }
             serviceClass.metaClass.withCASSpringSecurityRest = { String serviceUrl,String method = "GET",def query=[:],String springSecurityBaseUrl ->
                return cASRestServiceCallService.executeCASService(serviceUrl,method,query,springSecurityBaseUrl)
             }
        }        
        for (controllerClass in application.controllerClasses) {
             controllerClass.metaClass.withCASRest = { String serviceUrl,String method = "GET",def query=[:] ->
                return cASRestServiceCallService.executeCASService(serviceUrl,method,query)
             }
             controllerClass.metaClass.withCASSpringSecurityRest = { String serviceUrl,String method = "GET",def query=[:],String springSecurityBaseUrl ->
                return cASRestServiceCallService.executeCASService(serviceUrl,method,query,springSecurityBaseUrl)
             }
        }        
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
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
