//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.plantumlbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.PluginResult

class PlantUmlBuilderCompPlugin extends NodeBuilderPlugin {
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
                        case 'package':
                        case 'node':
                        case 'folder':
                        case 'database':
                        case 'cloud':
                        case 'frame':
                                if (postProcess) {
                                        out.printIndent()
                                        out.println("}")
                                } else {
                                        out.printIndent()
                                        def name = node.name
                                        def value = node.value ? " \"${node.value}\"" : ''
                                        out.println("${name}${value} {")
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'component':
                                if (!postProcess) {
                                        out.printIndent()
                                        def asText = node.attributes?.as ? " as ${node.attributes.as}" : ""
                                        out.println("${node.name} [${node.value}]${asText}${node.attributes?.stereotype ? " << $node.attributes.stereotype >>" : ""}")
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'link':
                                if (!postProcess) {
                                        if (!node.attributes.to) {
                                                throw new BuilderException("PlantUmlBuilderCompPlugin: ${BuilderNode.getNodePath(node)} does not have 'to' attribute!")
                                        }
                                        def linkType = node.attributes?.type ?: '-->'
                                        def description = node.attributes?.description ? " : ${node.attributes.description}" : ""
                                        out.println("${node.value} ${linkType} ${node.attributes.to}${description}")
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
