//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.plantumlbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.PluginResult
import org.bitbucket.novakmi.nodebuilder.BuilderException

class PlantUmlBuilderSeqPlugin extends NodeBuilderPlugin {

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = process(node, false, opaque)
                return retVal
        }

        @Override
        protected PluginResult processNodeAfter(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = process(node, true, opaque)
                return retVal
        }

        private PluginResult process(BuilderNode node, boolean postProcess, Object opaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED
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
                                                throw new BuilderException("NodeBuilderPlugin: ${BuilderNode.getNodePath(node)} close can be only 'deactivate' or 'destroy', not ${node.attributes.close}")
                                                break
                                }
                        }
                }

                def process = {val ->
                        if (!postProcess) {
                                out.printIndent()
                                out.println(val)
                                retVal = PluginResult.PROCESSED_FULL
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
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'msgAd': // alias for msg(..., close:'deactivate')
                                if (!node.attributes.close) {
                                        node.attributes.close = 'deactivate'
                                }
                        case 'msg': // 'msg'
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
                                retVal = PluginResult.PROCESSED_FULL
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
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'ref':
                                if (!postProcess) {
                                        if (!node.attributes.over) {
                                                throw new BuilderException("NodeBuilderPlugin: ${BuilderNode.getNodePath(node)} 'ref' requires 'over' attribute")
                                        } else {
                                                process("$node.name over ${node.attributes.over.join(',')} : $node.value")
                                                retVal = PluginResult.PROCESSED_FULL
                                        }
                                } else {
                                        retVal = PluginResult.PROCESSED_FULL
                                }
                                break
                        case 'else':
                                process("else $node.value")
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'divider':
                                process("== $node.value ==")
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'delay':
                                process("...$node.value...")
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'deactivate':
                        case 'destroy':
                                process("$node.name $node.value")
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        default:
                                retVal = PluginResult.NOT_ACCEPTED
                                break
                }

                return retVal
        }
}
