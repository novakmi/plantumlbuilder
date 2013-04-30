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

        private _makeLink(IndentPrinter out, from, to, type = null, description = null) throws BuilderException {
                if (from == null) {
                        throw new BuilderException("PlantUmlBuilderCompPlugin: from is null!")
                }
                if (to == null) {
                        throw new BuilderException("PlantUmlBuilderCompPlugin: to is null!")
                }
                def linkType = type ?: '-->'
                def linkDescription = description ? " : ${description}" : ""
                out.println("${from} ${linkType} ${to}${linkDescription}")
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
                                } else {
                                        if (node.attributes.link) { // create link/links
                                                def linkList = node.attributes.link instanceof List ? node.attributes.link : [node.attributes.link]
                                                linkList.each {linkMap ->
                                                        if (!linkMap instanceof Map) {
                                                                throw new BuilderException("PlantUmlBuilderCompPlugin: ${BuilderNode.getNodePath(node)} does ${linkMap} has to be link map!")
                                                        }
                                                        def from = node.attributes.as ?: node.value
                                                        _makeLink(out, from, linkMap.to, linkMap.type, linkMap.description)
                                                }
                                        }
                                }
                                retVal = PluginResult.PROCESSED_FULL
                                break
                        case 'link':
                                if (!postProcess) {
                                        if (!node.attributes.to) {
                                                throw new BuilderException("PlantUmlBuilderCompPlugin: ${BuilderNode.getNodePath(node)} does not have 'to' attribute!")
                                        }
                                        _makeLink(out, node.value, node.attributes.to, node.attributes?.type, node.attributes?.description)
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
