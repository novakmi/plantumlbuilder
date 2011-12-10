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

import cz.atlas.bubbles.it.nodebuilder.SimpleNode
import cz.atlas.bubbles.it.nodebuilder.PluginSimpleNodeBuilderListener
import cz.atlas.bubbles.it.nodebuilder.PluginListenerResult

class PlantUmlBuilderClassPlugin extends PluginSimpleNodeBuilderListener {
        @Override PluginListenerResult process(SimpleNode node, boolean postProcess, Object opaque) {
                PluginListenerResult retVal = PluginListenerResult.NOT_ACCEPTED
                IndentPrinter out = (IndentPrinter) opaque
                switch (node.name) {
                        case 'pclass':
                        case 'pinterface':
                        case 'penum':
                                if (postProcess) {
                                        if (node.attributes?.members) {
                                                out.printIndent()
                                                out.println("}")
                                        }
                                } else {
                                        out.printIndent()
                                        def name = node.name[1..-1] //skip starting 'p'
                                        def asText = node.attributes?.as ? " as \"${node.attributes.as}\"" : ""
                                        out.println("$name ${node.value}${node.attributes?.stereotype ? " << $node.attributes.stereotype >>" : ""}${asText}${node.attributes?.members ? " {" : ""}")
                                        if (node.attributes?.members) {
                                                out.incrementIndent()
                                                node.attributes?.members.each {
                                                        out.printIndent()
                                                        out.println("$it")
                                                }
                                                out.decrementIndent()
                                        }
                                }
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                        case 'relation':
                                if (!postProcess) {
                                        out.printIndent()
                                        out.println("${node.value} ${node.attributes.rel} ${node.attributes.to}${node.attributes.text ? " :${node.attributes.text}" : ""}")
                                }
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                        case 'ppackage':
                                out.printIndent()
                                if (!postProcess) {
                                        out.println("package ${node.value}")
                                } else {
                                        out.println("end package")
                                }
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break;
                }
                return retVal
        }
}
