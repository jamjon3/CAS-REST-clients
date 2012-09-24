//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//
// ant.mkdir(dir: "${basedir}/grails-app/CASRestServices")


ant.copy(file: "${pluginBasedir}/src/samples/CASRestClientConfig.groovy",
         todir: "${basedir}/grails-app/conf")

//target(main: "Creates artifacts for the CAS Rest Client plugin (USF)") {
//    def appDir = "${basedir}/grails-app"
//    def configFile = new File(appDir, 'conf/Config.groovy')
//    def appName = Ant.project.properties.'base.name'    
//    
//    if (configFile.exists()) {
//        configFile.withWriterAppend {
//            it.writeLine '\n// Added by the CAS Rest Client (USF) plugin:'        
//            it.writeLine "CASRestClient.cas.server = 'https://authtest.it.usf.edu'"
//            it.writeLine "CASRestClient.cas.ticketsPath = '/v1/tickets'"
//            it.writeLine "CASRestClient.cas.username = 'YourCASUserName'"    
//            it.writeLine "CASRestClient.cas.password = 'YourCASPassword'"                  
//        }
//    }
//    
//    ant.echo """
//    ********************************************************
//    * Your grails-app/conf/Config.groovy has been updated. *
//    *                                                      *
//    * Please verify that the values are correct.           *
//    ********************************************************
//    """    
//}
//
//setDefaultTarget(main)
