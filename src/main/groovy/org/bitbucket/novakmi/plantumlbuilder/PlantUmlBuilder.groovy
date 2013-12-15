//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.plantumlbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.TextPluginTreeNodeBuilder

// Builder class
class PlantUmlBuilder extends TextPluginTreeNodeBuilder {

        /**
         * Create new PlantUmlBuilder
         * @param indent number of spaces for indentation (default is 2)
         * @param plugins list of plugins to be added (no plugins by default)
         */
        public PlantUmlBuilder(indent = 2, plugins = null) {
                super(indent, plugins)
        }

        @Override
        protected void processNode(BuilderNode node, Object opaque) throws BuilderException {
                switch (node.name) {
                        case 'plant':
                                opaque.printIndent()
                                opaque.println(node.value)
                                break
                        case 'title':
                                opaque.printIndent()
                                opaque.println("title $node.value")
                                break
                        case 'note':
                                opaque.printIndent()
                                opaque.print(node.name)
                                if (node.attributes.as) {
                                        opaque.println(" $node.value as $node.attributes.as")
                                } else {
                                        opaque.println(" ${node.attributes.pos ? "${node.attributes.pos} : " : ''}$node.value")
                                }
                                break
                        case 'plantuml':
                                if (root == node) {
                                        opaque.setIndentLevel(-1) //do not indent children under 'plantuml' node
                                        break
                                }
                                throw new BuilderException("Node: ${BuilderNode.getNodePath(node)} must be root node!")
                        default:
                                throw new BuilderException("Node: ${BuilderNode.getNodePath(node)} is not recognized by the PlantUmlBuilder builder!")
                                break
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
        @Override
        public String getText(params = null) throws BuilderException {
                def umlval = ''
                if (root?.value) {
                        umlval = " $root.value"
                }
                def retVal = ''
                if (!params?.plainPlantUml) {
                        retVal += "@startuml${umlval}\n"
                }
                retVal += getTextBuffer()
                if (!params?.plainPlantUml) {
                        retVal += "@enduml"
                }
                return retVal
        }
}
