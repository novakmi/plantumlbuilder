//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.plantumlbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.PluginResult

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

        private void processClose(node, part,  out) {
                if (node.attributes.close) {
                        switch (node.attributes.close) {
                                case 'deactivate':
                                case 'destroy':
                                        out.printIndent()
                                        out.println("${node.attributes.close} $part")
                                        break
                                default:
                                        throw new BuilderException("NodeBuilderPlugin: ${BuilderNode.getNodePath(node)} close can be only 'deactivate' or 'destroy', not ${node.attributes.close}")
                                        break
                        }
                }
        }

        private def invertType(returnType) {
                def inverted = returnType.reverse()
                if (inverted.contains("<")) {
                        inverted = inverted.replace("<", ">")
                } else {
                        inverted = inverted.replace(">", "<")
                }
                return inverted
        }

        private PluginResult processMsg(BuilderNode node, boolean postProcess, Object opaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED
                IndentPrinter out = (IndentPrinter) opaque
                def value = node.value
                def to = node.attributes.to
                def noReturn = node.attributes.noReturn
                if (!to) { // self message
                        if (value == "[" || value == "]") {
                               throw new BuilderException("NodeBuilderPlugin: ${BuilderNode.getNodePath(node)} $value requires 'to' attribute!")
                        }
                        to = value
                        noReturn = true
                }
                def toOrig = to
                def activate = node.attributes.activate || node.attributes.close  // close implies activate
                def sp1 = " "
                def sp2 = " "
                if (!postProcess) {
                        def text = node.attributes.text
                        def type = node.attributes.type
                        if (value == "[") {
                                sp1 = ""
                        }
                        if (value == "]") { // for right side switch value with to
                                sp2 = ""
                                def tmp = to
                                to = value
                                value = tmp
                        }
                        if (!type) type = '->'
                        out.printIndent()
                        out.println("$value${sp1}$type${sp2}$to${text ? " : $text" : ''}")
                        if (activate) {
                                def actval = "" //used to add e.g. color
                                if ((node.attributes?.activate instanceof GString) ||
                                        (node.attributes?.activate instanceof String)) {
                                        actval = " ${(node.attributes.activate.value)}"
                                }
                                out.printIndent()
                                out.println("activate ${toOrig}${actval}")
                        }
                } else {
                        if (node.attributes.returnText || node.attributes.returnType || !noReturn) {
                                def invert = false
                                if (value == "[") {
                                        def tmp = new String(to)
                                        to = new String(value)
                                        value = tmp
                                        sp1 = ""
                                        invert = true
                                }
                                if (value == "]") { // for right side switch value with to
                                        sp2 = ""
                                        invert = true
                                }
                                def returnText = node.attributes.returnText
                                def returnType = node.attributes.returnType
                                if (!returnType) returnType = '-->'
                                if (invert) {
                                        returnType = invertType(returnType)
                                }
                                out.printIndent()
                                out.println("$to${sp1}$returnType${sp2}${value}${returnText ? " : $returnText" : ''}")
                                if (to == "[") {
                                        to = value //put back for deactivation
                                }
                        }
                        processClose(node, to, out)  // sets retVal to FAILED
                }
                retVal = PluginResult.PROCESSED_FULL
                return retVal
        }

        private PluginResult process(BuilderNode node, boolean postProcess, Object opaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED
                IndentPrinter out = (IndentPrinter) opaque

                def process = {val ->
                        if (!postProcess) {
                                out.printIndent()
                                out.println(val)
                                retVal = PluginResult.PROCESSED_FULL
                        }
                }

                switch (node.name) {
                        case 'actor':
                        case 'boundary':
                        case 'control':
                        case 'entity':
                        case 'database':
                        case 'participant':
                                if (!postProcess) {
                                        out.printIndent()
                                        out.print(node.name)
                                        out.println(" ${node.value}${node?.attributes?.as ? " as ${node.attributes.as}" : ""}"+
                                                    "${node?.attributes?.stereotype ? " <<${node.attributes.stereotype}>>" : ""}${node.attributes.color ? " ${node.attributes.color}" : ""}")
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'activate':
                                if (!postProcess) {
                                        out.printIndent()
                                        out.print("activate ")
                                        out.println(node.value)
                                } else {
                                        processClose(node, node.value, out) // sets retVal to FAILED
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'msgAd': // alias for msg(..., close:'deactivate')
                                if (!node.attributes.close) {
                                        node.attributes.close = 'deactivate'
                                }
                        case 'msg': // 'msg'
                                retVal = processMsg(node, postProcess, opaque)
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
                        case 'box':  // optional value = box text, optional color attribute
                                out.printIndent()
                                if (!postProcess) {
                                        def textString = node.value ? " \"$node.value\"" : ""
                                        def colorString = node.attributes.color ? " ${node.attributes.color}" : ""
                                        out.println("$node.name${textString}${colorString}")
                                } else {
                                        out.println("end box")
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
                        case 'rnote':
                        case 'hnote':
                                if (!postProcess) {
                                        if (!node.attributes.pos) {
                                                throw new BuilderException("NodeBuilderPlugin: ${BuilderNode.getNodePath(node)} '${node.name}' requires 'pos' attribute")
                                        }
                                        opaque.printIndent()
                                        opaque.print(node.name)
                                        opaque.println(" ${node?.attributes.pos ? "${node.attributes.pos} : " : ''}$node.value")
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        default:
                                retVal = PluginResult.NOT_ACCEPTED
                                break
                }

                return retVal
        }
}
