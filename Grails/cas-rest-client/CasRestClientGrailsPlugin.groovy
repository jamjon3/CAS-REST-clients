import edu.usf.CasRestClient.*

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CasRestClientGrailsPlugin {
    // the plugin version
    def version = "0.3.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [rest:'0.7 > *']
    def pluginExcludes = [
        "grails-app/views/**",
        "test/**",
        "src/docs/**"
    ]

    def observe = ['controllers','services']

    // TODO Fill in these fields
    def title = "CAS Rest Client Plugin" // Headline display name of the plugin
    def author = "James Jones"
    def authorEmail = "james@mail.usf.edu"
    def description = '''\
The CAS Rest Client Plugin provides a mechanism to execute REST based services protected by CAS and CAS Spring Security.

Usage
-----

The plugin will inject the following dynamic methods in both services and controllers:

 * `withCasRest(String serviceUrl,String method = "GET",String username,String password,def headers=[:],def query=[:])` - executes a service wrapped in CAS using the REST plugin
 * `withCasSpringSecurityRest(String serviceUrl,String method = "GET",String username,String password,def headers=[:],def query=[:],String springSecurityBaseUrl)` - executes a service wrapped in CAS Spring Security using the REST plugin
 
The properties are described as:

 * serviceUrl - The full URL used for the service
 * method - The HTTP method to use (GET,PUT,DELETE,POST). The default is "GET"
 * username - The CAS username that can access this service
 * password - The password associated with the CAS username
 * headers - Any additional header you may need to pass. Apps like Grails use 'Accept' and 'Content' headers to control JSON/XML input and output so those may be needed.
 * query - A hashmap of key/value pairs to be passed to the service
 * springSecurityBaseUrl - The base URL of your app that is using Spring Security (not the full service URL, this is used to get the JSESSIONID for spring security)

Configuration
-------------

No configuration required. The CAS server is determined on the 302 redirect if the protected service wants CAS credentials.


By default, a `CasRestClientConfig.groovy` file is added to your configuration directory. 

You can simply `add` this to your Config.groovy config locations with:

grails.config.locations = [ "file:${basedir}/grails-app/conf/CasRestClientConfig.groovy" ]

... or you can externalize this like any other config file

Generally, this file doesn't require modification to get started. However, if you want to setup a custom key store or you have a custom ticket path you need to be using for CAS
that is not the typical `/v1/tickets`, you can tweak it there. If you want to set a "global" default username & password, you can set that too (they get used when you send empty 
username and passwords).

The default options in the file looks like this:

    /** SSL key & truststore configuration key */
    rest.https.truststore.path = 'resources/certs/rest_client_keystore.jks'
    rest.https.keystore.path='resources/certs/rest_client_keystore.jks'
    /** Certificate Hostname Verifier configuration key */
    rest.https.cert.hostnameVerifier = 'ALLOW_ALL'
    /** Enforce SSL Socket Factory */
    rest.https.sslSocketFactory.enforce = true
    /** The CAS server tickets path **/
    casRestClient.cas.ticketsPath = "/v1/tickets"         
    /** Optional Global CAS username - rather than specifying it on each call **/ 
    // casRestClient.cas.username = "mycasusername"        
    /** Optional Global CAS password - rather than specifying it on each call **/ 
    // casRestClient.cas.password = "mycaspassword"        

Example
-------

def result = withCasRest("https://dev.it.usf.edu/nams/ws_convert","GET","myCasUsername","myCasPassword",[:],[submit_type:"netid",return_type:"mail","return":"json",value:"james"])


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

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "JIRA", url: "https://bullpen.jira.com/browse/CASREST" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://bullpen.jira.com/svn/CASREST" ]

    def doWithSpring = {
        casRestServiceCallService(CasRestServiceCallService)
    }

    def doWithDynamicMethods = { ctx -> processArtifacts(ctx) }

    def onChange = { event -> processArtifacts(event.ctx) }

    def onConfigChange = { event ->  processArtifacts(event.ctx) }

    private processArtifacts(ctx) {
        def config = ConfigurationHolder.config
        def application = ApplicationHolder.application

        def casRestServiceCallService = ctx.getBean('casRestServiceCallService')

        for (serviceClass in application.serviceClasses) {
             serviceClass.metaClass.withCasRest = { String serviceUrl,String method = "GET",String username,String password,def headers=[:],def query=[:] ->
                return casRestServiceCallService.executeCasService(serviceUrl,method,username,password,headers,query)
             }
             serviceClass.metaClass.withCasSpringSecurityRest = { String serviceUrl,String method = "GET",String username,String password,def headers=[:],def query=[:],String springSecurityBaseUrl ->
                return casRestServiceCallService.executeCasService(serviceUrl,method,username,password,headers,query,springSecurityBaseUrl)
             }
        }        
        for (controllerClass in application.controllerClasses) {
             controllerClass.metaClass.withCasRest = { String serviceUrl,String method = "GET",String username,String password,def headers=[:],def query=[:] ->
                return casRestServiceCallService.executeCasService(serviceUrl,method,username,password,headers,query)
             }
             controllerClass.metaClass.withCasSpringSecurityRest = { String serviceUrl,String method = "GET",String username,String password,def headers=[:],def query=[:],String springSecurityBaseUrl ->
                return casRestServiceCallService.executeCasService(serviceUrl,method,username,password,headers,query,springSecurityBaseUrl)
             }
        }   
    }
}
