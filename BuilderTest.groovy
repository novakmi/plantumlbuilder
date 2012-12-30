//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/plantumlbuilder/src/LICENSE)

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