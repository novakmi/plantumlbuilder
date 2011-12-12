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

import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin
import net.sourceforge.plantuml.SourceStringReader
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin


def callFunction(builder) {
    builder.plant('B->C')
    builder.activate('C', close: 'deactivate') {
        plant('C-->B')
    }
}

def builder = new PlantUmlBuilder()
builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderSeqPlugin()) // add extra support for Seq. diagrams


builder.plantuml {
    def clsr = {
        plant('A-->B')
    }
    plant("A->B") // this just copies text to the output, this allows any PlantUML expression
    activate('B', close: 'destroy') { // customized builder command activate (see  PlantUmlBuilderSeqPlugin)
        callFunction(builder)
        clsr()
    } // deactivate added automatically
}

println builder.getText()
println ''
println 'Without start/end'
println builder.getText(plainPlantUml: true)

SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream("./plantUmlBuilderTest.png")
s.generateImage(file);
file.close()
println ''

builder.reset()
builder.plantuml {
    msg('A', to: 'B', close: 'deactivate') {
        msg('B', to: 'C', type: "->>", returnType: "--\\", close: 'deactivate') {
            plant('C->D')
        }
    }
}
println builder.getText()
s = new SourceStringReader(builder.getText())
file = new FileOutputStream("./plantUmlBuilderTest2.png")
s.generateImage(file);
file.close()