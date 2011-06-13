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

package cz.atlas.bubbles.it.plantumlbuilder

/**
 * Builder node
 */
private class Node {
    def name = ''   // name of node (e.g. plant('text, at:'attrib') - name is plant)
    def parent = null // parent node (or null if this is root node)
    def attributes = [:] //attributes (e.g. plant('text, at:'atrib') - attributes.at == 'attrib')
    def value = null //value of node (e.g. plant('text, at:'attrib') - value is text)
    def children = [] //children nodes
}

//Listener interface  for node or attributes
enum PluginListenerResult {
    NOT_ACCEPTED, // node not accepted by plagin
    PROCESSED_STOP, // node processed by plugin, do not process node with other plugins
    PROCESSED_CONTINUE, // node processed by plugin, process node with other plugins as well
    FAILED, // node processing failed
}

interface PlantUmlBuilderPluginListener {
    /**
     * Process given node in plugin before and after plantuml builder
     * @param node builder node to process (
     * @param out IndentPrinter to print PlantUML text
     * @param postProcess if false, it is pre processing time, if true, it is post processing time
     * @return result
     * $see PluginListenerResult, Node
     */
    PluginListenerResult process(final Node node, IndentPrinter out, boolean postProcess)
}

// Builder class
class PlantUmlBuilder extends BuilderSupport {
    private IndentPrinter out
    private PrintWriter writer
    private StringWriter stringWriter
    private Node root = null //root node of the model

    //see http://groovy.codehaus.org/gapi/groovy/beans/ListenerList.html
    @groovy.beans.ListenerList
    private List<PlantUmlBuilderPluginListener> pluginListeners = []

    public PlantUmlBuilder() {
        stringWriter = new StringWriter()
        writer = new PrintWriter(stringWriter)
        out = new IndentPrinter(writer)
        out.decrementIndent() // to start from beg. of line
    }

    @Override protected void setParent(Object parent, Object child) {
        ((Node) parent).children.add(child)
        ((Node) child).parent = parent
        if (!root) root = parent
    }

    @Override protected Object createNode(Object name) {
        return new Node(name: name)
    }

    @Override protected Object createNode(Object name, Object value) {
        return new Node(name: name, value: value)
    }

    @Override protected Object createNode(Object name, Map attributes) {
        return new Node(name: name, attributes: attributes)
    }

    @Override protected Object createNode(Object name, Map attributes, Object value) {
        return new Node(name: name, value: value, attributes: attributes)
    }

    /*
    // not needed anymore because of   @groovy.beans.ListenerList, use  addPlantUmlBuilderListener
    def addListener(final PlantUmlBuilderPluginListener listener) {
        pluginListeners += listener
    }
    */

    def private printNode(node) {
        boolean nodeProcessedByListener = false
        boolean failed = false
        for (l in pluginListeners) {
            PluginListenerResult res = l.process(node, out, false)
            if (res == PluginListenerResult.FAILED) {
                failed = true
                break
            }
            nodeProcessedByListener = (res == PluginListenerResult.PROCESSED_STOP || res == PluginListenerResult.PROCESSED_CONTINUE)
            if (res == PluginListenerResult.PROCESSED_STOP) {
                break
            }
        }
        if (!nodeProcessedByListener && !failed) {
            switch (node.name) {
                case 'plant':
                    out.printIndent()
                    out.println(node.value)
                    break
                case 'title':
                    out.printIndent()
                    out.println("title $node.value")
                    break
                case 'actor':
                case 'participant':
                    out.printIndent()
                    out.print(node.name)
                    if (node.attributes.as) {
                        out.println(" $node.value as $node.attributes.as")
                    } else {
                        out.println(" $node.value")
                    }
                    break
                case 'note':
                    out.printIndent()
                    out.print(node.name)
                    if (node.attributes.as) {
                        out.println(" $node.value as $node.attributes.as")
                    } else {
                        out.println(" ${node.attributes.pos ? "${node.attributes.pos} : " : ''}$node.value")
                    }
                    break
                case 'plantuml':
                    if (root == node) {
                        break
                    }
                default:
                    println "Unsupported node name ${node.name}"
                    failed = true
                    break
            }
        }
        node.children.each {
            out.incrementIndent()
            printNode(it)
            out.decrementIndent()
        }
        if (nodeProcessedByListener && !failed) {
            for (l in pluginListeners) {
                PluginListenerResult res = l.process(node, out, true)
                if (res == PluginListenerResult.FAILED) {
                    failed = true
                    break
                }
                if (res == PluginListenerResult.PROCESSED_STOP) {
                    break
                }
            }
        }
    }

    /**
     * Get PlantUML text build by the builder
     * @param params map with optional name params.
     *         Currently supported 'plainPlantUml' - do not add '@startuml/@enduml' to the returned PlantUML text
     *         getText()
     *         getText(plainPlantUml: true)
     * @return build text
     */
    public String getText(params) {
        StringBuffer buffer = stringWriter.getBuffer()
        buffer.delete(0, buffer.length()) // clear buffer
        stringWriter.flush()
        def umlval = ''
        if (root) {
            printNode(root)
            if (root.value) {
                umlval = " $root.value"
            }
        }
        def retVal = ''
        if (!params?.plainPlantUml) {
            retVal += "@startuml${umlval}\n"
        }
        retVal += buffer.toString()
        if (!params?.plainPlantUml) {
            retVal += "@enduml"
        }
        return retVal
    }

    /**
     * Reset root element of the builder.
     * Use this method to start building PlantUML text from the beginning.
     */
    public void reset() {
        root = null
    }

}
