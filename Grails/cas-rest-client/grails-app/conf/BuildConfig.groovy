grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
  inherits('global') {
    excludes 'commons-codec' // Grails ships with 1.3, need 1.4
  }
  legacyResolve true
  checksums true
  log 'error'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.18'
        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
                excludes "commons-logging", "xml-apis", "groovy"
        }        
    }

    plugins {
        compile(":rest:0.7")
    }
}
