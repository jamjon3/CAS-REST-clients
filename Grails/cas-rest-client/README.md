CAS Rest Client Plugin
===============

The CAS Rest Client Plugin provides a mechanism to execute REST based services protected by CAS and CAS Spring Security.

##Usage

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

##Configuration

No configuration required. The CAS server is determined on the 302 redirect if the protected service wants CAS credentials.


By default, a `CasRestClientConfig.groovy` file is added to your configuration directory.

You can simply `add` this to your Config.groovy config locations with:

```
grails.config.locations = [ "file:${basedir}/grails-app/conf/CasRestClientConfig.groovy" ]
```

... or you can externalize this like any other config file

Generally, this file doesn't require modification to get started. However, if you want to setup a custom key store or you have a custom ticket path you need to be using for CAS
that is not the typical `/v1/tickets`, you can tweak it there. If you want to set a "global" default username & password, you can set that too (they get used when you send empty
username and passwords).

The default options in the file looks like this:

```
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
```

##Example

```
def result = withCasRest("https://dev.it.usf.edu/nams/ws_convert","GET","myCasUsername","myCasPassword",[:],[submit_type:"netid",return_type:"mail","return":"json",value:"james"])
```

The result will be as a string:

```
{"response":"success","netid":"james","mail":"james@usf.edu"}
```

You will have to do any type conversion to JSON,XML,etc as the output is a raw string.class.


