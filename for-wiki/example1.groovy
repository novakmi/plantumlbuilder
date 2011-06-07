/*
Copyright (c) 2011 bubbles.way@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

/*
Use groovy 1.8.0+
Classpath to PlantUMLBuilder and plantuml.jar has to be set, e.g.:
groovy -cp ~/sw/PlantUml/plantuml.jar:../src/main/groovy/ example1.groovy
*/

import net.sourceforge.plantuml.SourceStringReader
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilder

// create new builder
def builder = new PlantUmlBuilder()
// plantuml element is a root element of PlantUML
builder.plantuml {
            plant('A->B') // plant element only puts text to the oputput
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