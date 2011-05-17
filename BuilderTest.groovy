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
groovy -cp ~/sw/PlantUml/plantuml.jar:src/main/groovy/ BuilderTest.groovy
*/

import cz.atlas.bubbles.it.PlantUmlBuilder
import cz.atlas.bubbles.it.PlantUmlBuilderSeqListener
import net.sourceforge.plantuml.SourceStringReader


def callFunction(builder) {
    builder.plant('B->C')
    builder.activatebl('C') {
        plant('C-->B')
    }
}

def builder = new PlantUmlBuilder()
builder.addListener(new PlantUmlBuilderSeqListener()) // add extra support for Seq. diagrams


builder.plantuml {
    def clsr = {
        plant('A-->B')
    }
    plant("A->B") // this just copies text to the output, this allows any PlantUML expression
    activatebl('B') { // customized builder command activate (see  PlantUmlBuilderSeqListener)
        callFunction(builder)
        clsr()
    } // deactivate added automatically
}

println builder.getText()
println ''
println 'Without start/end'
println builder.getText(false)

SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream("./plantUmlBuilderTest.png")
s.generateImage(file);
file.close()
println ''
builder = new PlantUmlBuilder() // new instance
builder.addListener(new PlantUmlBuilderSeqListener()) // add extra support for Seq. diagrams
builder.plantuml {
    callbl(from: 'A', to: 'B', activate: true) {
        callbl(from: 'B', to: 'C', activate: true, type: "->>", rettype: "--\\") {
            plant('C->D')
        }
    }
}
println builder.getText()
s = new SourceStringReader(builder.getText())
file = new FileOutputStream("./plantUmlBuilderTest2.png")
s.generateImage(file);
file.close()