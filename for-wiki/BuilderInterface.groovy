//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

/*
Classpath to PlantUMLBuilder and plantuml.jar has to be set, e.g.:
groovy -cp ~/sw/PlantUml/plantuml.jar:../src/main/groovy/ example1.groovy
*/

import net.sourceforge.plantuml.SourceStringReader
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.PluginResult
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin

// create new builder
def builder = new PlantUmlBuilder()
builder.registerPlugin(new PlantUmlBuilderClassPlugin())
// plantuml element is a root element of PlantUML
builder.plantuml {
    def builderMembers = [
        '-@groovy.beans.ListenerList\\nList<NodeBuilderPlugin> pluginListeners',
        '+String getText(params)',
        '+void reset()',
        '+void addPlantUmlBuilderPluginListener(NodeBuilderPlugin listener)',
        '+void removePlantUmlBuilderPluginListener(NodeBuilderPlugin listener)',
    ]
    pclass(PlantUmlBuilder.class.name, members: builderMembers)
    pinterface(NodeBuilderPlugin.class.name, members: ['+PluginResult processNodeBefore(final BuilderNode node, IndentPrinter out, boolean postProcess)'])
    penum(PluginResult.class.name, members: [
        PluginResult.NOT_ACCEPTED,
        PluginResult.PROCESSED,
        PluginResult.FAILED,
    ])
    relation(PlantUmlBuilder.class.name, rel: '*-- "0..*"', to: NodeBuilderPlugin.class.name)
    def nodeMembers = [
        "+name // name of node",
        "+parent // parent node",
        "+attributes = [:]",
        "+value",
        "+children = [] //children nodes",
    ]
    pclass(BuilderNode.class.name, members: nodeMembers)
}

// use plantUML to create png file from plantuml text
SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream('./plantbuilderinterface.png')
s.generateImage(file)
file.close()
