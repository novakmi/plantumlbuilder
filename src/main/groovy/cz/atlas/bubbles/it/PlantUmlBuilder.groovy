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

package cz.atlas.bubbles.it

//Builder model node
private class Node {
    def name = ''
    def parent = null; // parent (or null if this is root node)
    def attributes = [:] //attributes
    def value = null //value
    def children = [] //children nodes
}

//Listener interface  for node or attributes
enum ListenerResult {
    NOT_ACCEPTED,
    PROCESSED,
    FAILED,
}

interface PlantBuilderListener {
    ListenerResult process(final Node node, IndentPrinter out, boolean postProcess)
}

// Builder class
class PlantUmlBuilder extends BuilderSupport {
    private IndentPrinter out
    private PrintWriter writer
    private StringWriter stringWriter
    private Node root = null //root node of the model

    private List listeners = []

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

    def addListener(final PlantBuilderListener listener) {
        listeners += listener
    }

    def private printNode(node) {
        boolean nodeProcessedByListener = false
        for (l in listeners) {
            ListenerResult res = l.process(node, out, false)
            nodeProcessedByListener = (res == ListenerResult.PROCESSED)
        }
        if (!nodeProcessedByListener) {
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
                    if (node.attributes.text) {
                        out.println(" $node.attributes.text as $node.value")
                    } else {
                        out.println(" $node.value")
                    }
                    break
                case 'note':
                    out.printIndent()
                    def pos = node.attributes.pos?:'right'
                    out.println("note $pos : $node.value")
                    break
                case 'plantuml':
                    if (root == node) {
                        break
                    }
                default:
                    println "Unsupported node name ${node.name}"
                    break
            }
        }
        node.children.each {
            out.incrementIndent()
            printNode(it)
            out.decrementIndent()
        }
        if (nodeProcessedByListener) {
            for (l in listeners) {
                ListenerResult res = l.process(node, out, true)
                nodeProcessedByListener = (res == ListenerResult.PROCESSED)
            }
        }
    }

    public String getText(embedStartEnd = true) {
        StringBuffer buffer = stringWriter.getBuffer()
        buffer.delete(0, buffer.length()) // clear buffer
        stringWriter.flush()
        if (root) {
            printNode(root)
        }
        def retVal = ''
        if (embedStartEnd) {
            retVal+= "@startuml\n"
        }
        retVal += buffer.toString()
        if (embedStartEnd) {
            retVal+= "@enduml"
        }
        return retVal
    }

}
