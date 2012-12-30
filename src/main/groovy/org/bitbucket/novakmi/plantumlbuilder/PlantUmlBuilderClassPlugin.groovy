//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.plantumlbuilder

import org.bitbucket.novakmi.nodebuilder.PluginResult
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode

class PlantUmlBuilderClassPlugin extends NodeBuilderPlugin {
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
                switch (node.name) {
                        case 'pabstract':
                        case 'paclass':
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
                                        if (name == 'aclass') {
                                                name = 'abstract class'
                                        }
                                        def asText = node.attributes?.as ? " as \"${node.attributes.as}\"" : ""
                                        out.println("$name ${node.value}${asText}${node.attributes?.stereotype ? " << $node.attributes.stereotype >>" : ""}${node.attributes?.members ? " {" : ""}")
                                        if (node.attributes?.members) {
                                                out.incrementIndent()
                                                node?.attributes?.members?.each {
                                                        out.printIndent()
                                                        out.println("$it")
                                                }
                                                out.decrementIndent()
                                        }
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'relation':
                                if (!postProcess) {
                                        out.printIndent()
                                        out.println("${node.value} ${node.attributes.rel} ${node.attributes.to}${node.attributes.text ? " :${node.attributes.text}" : ""}")
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'ppackage':
                                out.printIndent()
                                if (!postProcess) {
                                        out.println("package ${node.value}")
                                } else {
                                        out.println("end package")
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break;
                        default:
                                retVal = PluginResult.NOT_ACCEPTED
                                break
                }
                return retVal
        }
}
