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

class PlantUmlBuilderSeqPlugin extends PluginSimpleNodeBuilderListener {

        @Override PluginListenerResult process(SimpleNode node, boolean postProcess, Object opaque) {
                PluginListenerResult retVal = PluginListenerResult.NOT_ACCEPTED
                IndentPrinter out = (IndentPrinter) opaque
                def processClose = {to ->
                        if (node.attributes.close) {
                                switch (node.attributes.close) {
                                        case 'deactivate':
                                        case 'destroy':
                                                out.printIndent()
                                                out.println("${node.attributes.close} $to")
                                                break
                                        default:
                                                out.println("**** close can be only 'deactivate' or 'destroy', not ${node.attributes.close} ***")
                                                retVal = PluginListenerResult.FAILED
                                                break
                                }
                        }
                }

                def process = {val ->
                        if (!postProcess) {
                                out.printIndent()
                                out.println(val)
                                retVal = PluginListenerResult.PROCESSED_STOP
                        }
                }

                switch (node.name) {
                        case 'activate':
                                if (!postProcess) {
                                        out.printIndent()
                                        out.print("activate ")
                                        out.println(node.value)
                                } else {
                                        processClose(node.value) // sets retVal to FAILED
                                }
                                if (retVal != PluginListenerResult.FAILED) {
                                        retVal = PluginListenerResult.PROCESSED_STOP
                                }
                                break
                        case 'msg': // 'msg' is alias for message
                                def to = node.attributes.to
                                def noReturn = node.attributes.noReturn
                                if (!to) { // self message
                                        to = node.value
                                        noReturn = true
                                }
                                def activate = node.attributes.activate || node.attributes.close  // close implies activate
                                if (!postProcess) {
                                        def text = node.attributes.text
                                        def type = node.attributes.type
                                        if (!type) type = '->'
                                        out.printIndent()
                                        out.println("$node.value $type $to${text ? " : $text" : ''}")
                                        if (activate) {
                                                out.printIndent()
                                                out.println("activate $to")
                                        }
                                } else {
                                        if (node.attributes.returnText || node.attributes.returnType || !noReturn) {
                                                def returnText = node.attributes.returnText
                                                def returnType = node.attributes.returnType
                                                if (!returnType) returnType = '-->'
                                                out.printIndent()
                                                out.println("$to $returnType ${node.value}${returnText ? " : $returnText" : ''}")
                                        }
                                        processClose(to)  // sets retVal to FAILED
                                }
                                if (retVal != PluginListenerResult.FAILED) {
                                        retVal = PluginListenerResult.PROCESSED_STOP
                                }
                                break
                        case 'opt':
                        case 'loop':
                        case 'alt':
                        case 'group':
                        case 'par':
                        case 'break':
                        case 'critical':
                                out.printIndent()
                                if (!postProcess) {
                                        out.println("$node.name $node.value")
                                } else {
                                        out.println("end")
                                }
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                        case 'ref':
                                if (!node.attributes.over) {
                                        out.println("***** 'ref' requires 'over' attribute ****")
                                        retVal = PluginListenerResult.FAILED
                                } else {
                                        process("$node.name over ${node.attributes.over.join(',')} : $node.value")
                                        retVal = PluginListenerResult.PROCESSED_STOP
                                }
                                break
                        case 'else':
                                process("else $node.value")
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                        case 'divider':
                                process("== $node.value ==")
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                        case 'delay':
                                process("...$node.value...")
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                        case 'deactivate':
                        case 'destroy':
                                process("$node.name $node.value")
                                retVal = PluginListenerResult.PROCESSED_STOP
                                break
                }

                return retVal
        }
}
