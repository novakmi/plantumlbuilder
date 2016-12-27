#!/usr/bin/env groovy
//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8052')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '1.0.0')

// Without Internet connection, run as groovy script with jars in the classpath (-cp), comment @Grab ... annotations above
// 'groovy -cp plantumlbuilder-x.x.x.jar:plantuml-xxxx.jar:nodebuilder-x.x.x.jar  <scriptName>.groovy'

// This script example represents example of usage without any plugin

// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
A = 'A'
B = 'B'
C = 'C'

def uml = {
        // example of block reuse with inner closure (no need for <<)
        def interact = { a, b ->
                plant "$a->$b"
                plant "activate $b"
                plant "$b-->$a"
                plant "deactivate $b"
        }
        title 'Plantuml builder basic example - simple sequence diagram'
        plant 'participant "A node" as A'
        plant "participant B"
        plant "participant C"
        plant "$A->$B"
        plant "activate $B", {
                interact B, C //reuse
        }
        plant "$B-->$A"
        plant "deactivate $B"
        interact C, B //reuse
}

builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
fileName = "plant_seq.png" //name of file to generate
builder << {
        plantuml "${fileName}", {
                delegate << uml // apply uml closure
        }
}
println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image
