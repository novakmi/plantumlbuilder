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

import cz.atlas.bubbles.it.plantumlbuilder.ListenerResult
import cz.atlas.bubbles.it.plantumlbuilder.PlantBuilderListener
import cz.atlas.bubbles.it.plantumlbuilder.Node

class PlantUmlBuilderSeqListener implements PlantBuilderListener {

    ListenerResult process(Node node, IndentPrinter out, boolean postProcess) {
        ListenerResult retVal = ListenerResult.NOT_ACCEPTED
        switch (node.name) {
            case 'activatebl':
                out.printIndent()
                if (!postProcess) {
                    out.print("activate ")
                } else {
                    out.print('deactivate ')
                }
                out.println(node.value)
                retVal = ListenerResult.PROCESSED
                break
            case 'callbl':
                def from = node.attributes.from
                def to = node.attributes.to
                def activate = node.attributes.activate
                if (!postProcess) {
                    def text = node.attributes.text
                    def type = node.attributes.type
                    if (!type) type = '->'
                    out.printIndent()
                    out.println("$from $type $to${text ? " : $text" : ''}")
                    if (activate) {
                        out.printIndent()
                        out.println("activate $to")
                    }
                } else {
                    def rettext = node.attributes.rettext
                    def rettype = node.attributes.rettype
                    if (!rettype) rettype = '-->'
                    if (!node.attributes.noret) { // return not needed, e.g. self message
                        out.printIndent()
                        out.println("$to $rettype $from${rettext ? " : $rettext" : ''}")
                    }
                    if (activate) {
                        out.printIndent()
                        out.println("deactivate $to")
                    }

                }
                retVal = ListenerResult.PROCESSED
                break
            case 'opt':
            case 'loop':
            case 'alt':
            case 'group':
                out.printIndent()
                if (!postProcess) {
                    out.println("$node.name $node.value")
                } else {
                    out.println("end")
                }
                retVal = ListenerResult.PROCESSED
                break
             case 'ref':
                 if (!postProcess) {
                     // in attribute 'over' expect list of over elements
                     out.printIndent()
                     out.println("$node.name over ${node.attributes.over.join(',')} : $node.value")
                     retVal = ListenerResult.PROCESSED
                 }
                break
            case 'else':
                if (!postProcess) {
                    out.printIndent()
                    out.println("else $node.value")
                    retVal = ListenerResult.PROCESSED
                }
                break
            case 'divider':
                if (!postProcess) {
                    out.printIndent()
                    out.println("== $node.value ==")
                    retVal = ListenerResult.PROCESSED
                }
                break
        }
        return retVal
    }
}
