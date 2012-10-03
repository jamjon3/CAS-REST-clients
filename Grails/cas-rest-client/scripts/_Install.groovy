ant.mkdir(dir: "${basedir}/resources")

ant.move(file: "${pluginBasedir}/src/resources/CasRestClientConfig.groovy", todir: "${basedir}/grails-app/conf")
ant.move(file: "${pluginBasedir}/src/resources/rest_client_keystore.jks", todir: "${basedir}/resources/certs")