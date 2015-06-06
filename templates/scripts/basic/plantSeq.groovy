#!/usr/bin/env groovy
//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
//@GrabResolver(name = 'novakmirepo', root = 'https://github.com/novakmi/novakmirepo/raw/master/releases', m2Compatible = true)
//@GrabResolver(name = 'novakmirepo', root = 'http://dl.bintray.com/novakmi/maven', m2Compatible = true)
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8020')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.4.3')

// Without Internet connection, run as groovy script with jars in the classpath (-cp), comment @Grab ... annotations above
// 'groovy -cp plantumlbuilder-x.x.x.jar:plantuml-xxxx.jar:nodebuilder-x.x.x.jar  <scriptName>.groovy'

// This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder

//name of file to generate
final def fileName = "plant_seq.png"
// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
final def A = 'A'
final def B = 'B'
final def C = 'C'

builder.plantuml("${fileName}") {
        // example of block reuse
        def interact = {a, b ->
                plant "$a->$b"
                plant "activate $b"
                plant "$b-->$a"
                plant "deactivate $b"
        }
        title 'Plantuml builder basic template - simple sequence diagram'
        plant 'participant "A node" as A'
        plant "participant B"
        plant "participant C"
        plant "$A->$B"
        plant "activate $B", {
                interact(B, C) //reuse
        }
        plant "$B-->$A"
        plant "deactivate $B"
        interact(C, B) //reuse
}

println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image
