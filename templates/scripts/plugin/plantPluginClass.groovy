#!/usr/bin/env groovy
//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)
//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8052')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.4.4')

// Without Internet connection, run as groovy script with jars in the classpath (-cp), comment @Grab ... annotations above
// 'groovy -cp plantumlbuilder-x.x.x.jar:plantuml-xxxx.jar:nodebuilder-x.x.x.jar  <scriptName>.groovy'

// This script template represents example of usage with Class plugin
// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
abstractList = 'AbstractList'
abstractCollection = 'AbstractCollection'
list = 'List'
collection = 'Collection'
timeUnits = 'TimeUnits'

uml = {
        title "Plantuml builder plugin template"

        paclass abstractList
        pabstract abstractCollection
        pinterface list
        // e.g use pinterface instead of plant "interface $list", members can be also added  pinterface list, members: ['+get()', '+set()']
        pinterface collection
        plant '' // empty line

        relation list, rel: '<|--', to: abstractList  //can be used instead of plant "$list <|-- $abstractList"
        relation collection, rel: '<|--', to: abstractCollection
        plant ''

        relation collection, rel: '<|-', to: list
        relation abstractCollection, rel: '<|-', to: abstractList
        relation abstractList, rel: '<|--', to: java.util.ArrayList.class.name
        // use name of real class (we get automatically package)
        plant ''

        plant "${java.util.ArrayList.class.name} : ${java.lang.Object.class.name}[] elementData"
        plant "${java.util.ArrayList.class.name} : size()"

        penum timeUnits, members: ['DAYS', 'HOURS', 'MINUTES']
        //for enums (classes, interfaces), declare members are list

        plant "note  as N", {
                plant "class diagram (adapted from PlantUML documentation" // indentation
                plant "http://plantuml.sourceforge.net/)"
        }
        plant "end note"
}

builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
builder.registerPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin()) // add class diagram plugin support
fileName = "plant_plugin_class.png" //name of file to generate
builder << {
        plantuml "${fileName}", {
                delegate << uml // apply uml closure
        }
}
println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image
