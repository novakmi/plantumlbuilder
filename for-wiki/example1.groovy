//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

/*
Classpath to PlantUMLBuilder and plantuml.jar has to be set, e.g.:
groovy -cp ~/sw/PlantUml/plantuml.jar:nodebuilder-0.0.1.jar:plantumlbuilder-0.1.0.jar example1.groovy
*/

import net.sourceforge.plantuml.SourceStringReader
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder

// create new builder
def builder = new PlantUmlBuilder()
// plantuml element is a root element of PlantUML
builder.plantuml {
            plant('A->B') // plant element only puts text to the output
            plant('activate B')
            plant('B->C')
            plant('activate C')
            plant('C-->B')
            plant('deactivate C')
            plant('A-->B')
            plant('deactivate B')
}

println builder.getText() // get PlantUML text
println ''
println 'Without @startuml/@enduml'
println builder.getText(plainPlantUml: true)  // get PlantUML text without @startuml and @enduml

// use plantUML to create png file from plantuml tgext
SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream('./example1.png')
s.generateImage(file)
file.close()

builder.reset()
builder.plantuml {
        plant('A->B')
        plant('activate B') {
            plant('B->C')
            plant('activate C')
            plant('C-->B')
            plant('deactivate C')
        }
        plant('A-->B')
        plant('deactivate B')
}

println builder.getText() // get PlantUML text
s = new SourceStringReader(builder.getText())
file = new FileOutputStream('./example1-2.png')
s.generateImage(file)
file.close()