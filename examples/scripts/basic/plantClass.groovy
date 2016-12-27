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

// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
abstractList = 'AbstractList'
abstractCollection = 'AbstractCollection'
list = 'List'
collection = 'Collection'
timeUnits = 'TimeUnits'

uml = {
        title "Plantuml builder basic example"

        plant "abstract class $abstractList"
        plant "abstract $abstractCollection"
        plant "interface $list"
        plant "interface $collection"
        plant '' // empty line

        plant "$list <|-- $abstractList"
        plant "$collection <|-- $abstractCollection"
        plant ''

        plant "$collection <|- $list"
        plant "$abstractCollection <|- $abstractList"
        plant "$abstractList <|-- ${java.util.ArrayList.class.name}" // use name of real class (we get automatically package)
        plant ''

        plant "${java.util.ArrayList.class.name} : ${java.lang.Object.class.name}[] elementData"
        plant "${java.util.ArrayList.class.name} : size()"

        plant "enum $timeUnits"
        ['DAYS', 'HOURS', 'MINUTES'].each { e -> // generate in loop
                plant "$timeUnits : $e"
        }

        plant "note  as N", {
                plant "class diagram (adapted from PlantUML documentation" // indentation
                plant "http://plantuml.sourceforge.net/)"
        }
        plant "end note"
}

// This script example represents example of usage without any plugin
builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
fileName = "plant_class.png" //name of file to generate
builder << {
        plantuml "${fileName}", {
                delegate << uml // apply uml closure
        }
}
println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image
