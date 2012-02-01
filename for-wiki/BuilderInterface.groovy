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
        '+String getBuiltText(params)',
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
SourceStringReader s = new SourceStringReader(builder.getBuiltText())
FileOutputStream file = new FileOutputStream('./plantbuilderinterface.png')
s.generateImage(file)
file.close()
