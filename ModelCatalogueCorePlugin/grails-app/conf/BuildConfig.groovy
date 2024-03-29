grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: false,
    // test: [maxMemory: 2048, minMemory: 1024, debug: false, maxPerm: 512, daemon:true],
    // configure settings for the run-app JVM
    run: false,
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]



grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo 'http://jcenter.bintray.com'
		mavenRepo'http://dl.bintray.com/csfercoci/maven'
		mavenRepo "http://dl.bintray.com/metadata/model-catalogue"
        //mavenRepo "http://dl.dropbox.com/u/326301/repository"
		mavenRepo 'http://jasperreports.sourceforge.net/maven2'		
		//mavenRepo "http://www.biojava.org/download/maven/"


    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        compile 'com.google.guava:guava:18.0'
        compile 'net.sourceforge.owlapi:owlapi-oboformat:3.5.1'
        test 'xmlunit:xmlunit:1.6'
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
		
		//----- Jasper Reports specific dependencies 
		 //from the bintray
		compile 'net.sf.jasperreports:jasperreports-javaflow:6.0.3'
		compile 'commons-javaflow:commons-javaflow:20060411'
		
		compile ('net.sf.jasperreports:jasperreports:6.0.3'){
            excludes 'antlr', 'commons-logging','jasperreports',
                     'ant', 'mondrian' ,'barbecue', 'xml-apis-ext','xml-apis', 'xalan', 'groovy-all', 'hibernate', 'saaj-api', 'servlet-api',
                     'xercesImpl','xmlParserAPIs','spring-core','bsh', 'spring-beans', 'jaxen', 'barcode4j','batik-svg-dom','batik-xml','batik-awt-util','batik-dom',
                     'batik-css','batik-gvt','batik-script', 'batik-svggen','batik-util','batik-bridge','persistence-api','jdtcore','bcmail-jdk16','bcprov-jdk16','bctsp-jdk16',
                     'bcmail-jdk14','bcprov-jdk14','bctsp-jdk14','xmlbeans', 'olap4j'
        }
		 
    }

    plugins {
        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }

        runtime ':database-migration:1.3.6'

        compile ":asset-pipeline:1.9.9"
        compile ":coffee-asset-pipeline:1.9.0"
        compile ":less-asset-pipeline:1.10.0"
//        runtime ":hibernate4:4.3.5.5"
        runtime  ":hibernate:3.6.10.18"

        compile ":excel-export:0.2.1"
        compile ":executor:0.3"

        compile ":csv:0.3.1"

        test ':build-test-data:2.1.2'
        test ':fixtures:1.3'

        // codenarc static analysis
        build ":codenarc:0.20"

        // test coverage
        test ":code-coverage:1.2.7"

        build ":tomcat:7.0.55"
		//jasper report generator see in dependencies

    }
}


codenarc.reports = {
    MyXmlReport('xml') {                    // The report name "MyXmlReport" is user-defined; Report type is 'xml'
        outputFile = 'CodeNarc-Report.xml'  // Set the 'outputFile' property of the (XML) Report
        title = 'Sample Report'             // Set the 'title' property of the (XML) Report
    }
    MyHtmlReport('html') {                  // Report type is 'html'
        outputFile = 'CodeNarc-Report.html'
        title = 'Sample Report'
    }
}

// release
grails.project.repos.metadataSnapshots.url = "http://repository-metadata.forge.cloudbees.com/snapshot/"

