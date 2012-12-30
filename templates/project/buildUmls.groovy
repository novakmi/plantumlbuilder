#!/usr/bin/env groovy
//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/plantumlbuilder/src/LICENSE)


//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '7622')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.5.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.3.1')

//due to @Grab limitation in script, we have to have def ... after Grab, in our case we can just create builder
// see http://groovy.codehaus.org/Grapes+and+grab()
def builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
builder.registerPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin()) // add class diagram plugin support
builder.registerPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin()) // add seq. diagram plugin support

if (args) {  // arguments passed can be processed this way
        println "Following arguments have been passed:"
        args.each {a ->
                println(a)
        }
}

/**
 create List of classes that are related to generated uml files
 Each class has to have static function 'getName()' returning name of uml file (without uml extension) and
 static function  'buildUml' accepting empty builder as parameter
 */
def umls = [
    UmlClass,
    UmlSeq,
]

for (u in umls) { // one can also use umls.each {u->
        builder.reset() // empty builder
        u.buildUml(builder)
        print("Processing ${u.getName()} ...")
        new File("${u.getName()}.txt").write(builder.getText()) //write plantuml script to file
        new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${u.getName()}.png")) // create image
        println("done")
}


