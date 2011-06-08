/**
 * Created by IntelliJ IDEA.
 * User: mnl
 * Date: 6/7/11
 * Time: 11:17 PM
 * To change this template use File | Settings | File Templates.
 */

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
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilderClassPlugin
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilderPluginListener
import cz.atlas.bubbles.it.plantumlbuilder.PluginListenerResult
import cz.atlas.bubbles.it.plantumlbuilder.Node

// create new builder
def builder = new PlantUmlBuilder()
builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderClassPlugin())
// plantuml element is a root element of PlantUML
builder.plantuml {
    def builderMembers = [
        '-@groovy.beans.ListenerList\\nList<PlantUmlBuilderPluginListener> pluginListeners',
        '+String getText(params)',
        '+void reset()',
        '+void addPlantUmlBuilderPluginListener(PlantUmlBuilderPluginListener listener)',
        '+void removePlantUmlBuilderPluginListener(PlantUmlBuilderPluginListener listener)',
    ]
    plantclass(PlantUmlBuilder.class.name, members: builderMembers)
    note('See @groovy.beans.ListenerList', pos: "top of ${PlantUmlBuilder.class.name}")
    plantinterface(PlantUmlBuilderPluginListener.class.name, members: ['+PluginListenerResult process(final Node node, IndentPrinter out, boolean postProcess)'])
    plantenum(PluginListenerResult.class.name, members: [
        PluginListenerResult.NOT_ACCEPTED,
        PluginListenerResult.PROCESSED_STOP,
        PluginListenerResult.PROCESSED_CONTINUE,
        PluginListenerResult.FAILED,
    ])
    relation(PlantUmlBuilder.class.name, rel: '*-- "0..*"', to: PlantUmlBuilderPluginListener.class.name)
    def nodeMembers = [
        "+name // name of node",
        "+parent // parent node",
        "+attributes = [:]",
        "+value",
        "+children = [] //children nodes",
    ]
    plantclass(Node.class.name, members: nodeMembers)
}

// use plantUML to create png file from plantuml text
SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream('./plantbuilderinterface.png')
s.generateImage(file)
file.close()
