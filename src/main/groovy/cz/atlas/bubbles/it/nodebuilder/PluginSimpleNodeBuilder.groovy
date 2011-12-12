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

package cz.atlas.bubbles.it.nodebuilder

/**
 * Enumeration to be returned as result from plugin processing
 */
enum PluginResult {
        NOT_ACCEPTED, // node not accepted by the plugin (the plugin does not recognize this node)
        PROCESSED, // node processed by the plugin
        FAILED, // node processing failed (the plugin recognizes the node, but processing failed)
}

abstract class NodeBuilderPlugin {

        /**
         * Process given node by the plugin before node (and its children) is processed by the builder
         * Override this method in Plugin implementation, if needed.
         * @param node to process by the plugin
         * @param opaque object to be passed during processing of nodes
         * @param pluginMap plugin Map  (passed between all plugin processing - plugin can use it to store information,
         *                      to communicate with other plugins)
         * @return PluginResult
         * @see PluginResult , SimpleNode
         */
        protected PluginResult processNodeBefore(SimpleNode node, Object opaque, Map pluginMap) {
                return PluginResult.NOT_ACCEPTED
        }

        /**
         * Process given node by the plugin after node (and its children) is processed by the builder
         * Override this method in Plugin implementation, if needed.
         * @param node to process by the plugin
         * @param opaque object to be passed during processing of nodes
         * @param pluginMap plugin Map  (passed between to all plugin processing - plugin can use it to store information,
         *                      to communicate with other plugins)
         * @return PluginResult
         * @see PluginResult , SimpleNode
         */
        protected PluginResult processNodeAfter(SimpleNode node, Object opaque, Map pluginMap) {
                return PluginResult.NOT_ACCEPTED
        }

}

abstract class PluginSimpleNodeBuilder extends SimpleNodeBuilder {

        private List<NodeBuilderPlugin> plugins = []

        /**
         * Add plugin to the builder.
         * @param plugin
         */
        public void addPlugin(final NodeBuilderPlugin plugin) {
                plugins += plugin
        }

        /**
         * Private helper method to call method on plugin
         * @param method method name as string
         * @param node to process by the plugin
         * @param object to be passed during processing of nodes
         * @param pluginMap plugin Map  (passed between to all plugin processing - plugin can use it to store information,
         *                      to communicate with other plugins)
         * @return true ... ok, false ... error
         */
        private boolean processPlugins(String method, SimpleNode node, Object opaque, Map pluginMap) {
                boolean retVal = true
                for (l in plugins) {
                        PluginResult res = l."$method"(node, opaque, pluginMap)
                        if (res == PluginResult.FAILED) {
                                retVal = false
                                break
                        }
                }
                return retVal
        }

        /**
         * Process tree of nodes
         * This method can be overridden in derived implementation.
         * @param rootNode root node of the tree (can be also leaf)
         * @param node opaque object to be passed for node processing
         * @param pluginMap to be passed between tree and plugin processing, if null - empty map is created and passed
         * @return true ... ok, false .. failure
         */
        @Override protected boolean processTree(SimpleNode rootNode, Object opaque, Map pluginMap = null) {
                boolean retVal = true
                if (!pluginMap) {
                        pluginMap = [:]
                }
                // process all plugins  processNodeBefore
                retVal = processPlugins('processNodeBefore', rootNode, opaque, pluginMap)
                if (retVal) {
                        retVal = super.processTree(rootNode, opaque, pluginMap) // this will call our process tree recursively
                }
                // process all plugins  processNodeAfter
                if (retVal) {
                        retVal = processPlugins('processNodeAfter', rootNode, opaque, pluginMap)
                }
                return retVal
        }
}