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

 //Listener interface  for node or attributes
enum PluginListenerResult {
        NOT_ACCEPTED, // node not accepted by plugin
        PROCESSED_STOP, // node processed by plugin, do not process node with other plugins
        PROCESSED_CONTINUE, // node processed by plugin, process node with other plugins as well
        FAILED, // node processing failed
}

abstract class PluginSimpleNodeBuilderListener {
        /**
         * Process given node in plugin before and after plantuml builder
         * @param node builder node to process (
         * @param postProcess if false, it is pre processing time, if true, it is post processing time
         * @param opaque object to be passed from application using builder to plugin (can be null)
         * @return result
         * $see PluginListenerResult, SimpleNode
         */
        PluginListenerResult process(final SimpleNode node, boolean postProcess, Object opaque) {
                return PluginListenerResult.NOT_ACCEPTED
        }
}

abstract class PluginSimpleNodeBuilder extends SimpleNodeBuilder {

        private List<PluginSimpleNodeBuilderListener> pluginListeners = []

        def addListener(final PluginSimpleNodeBuilderListener listener) {
                pluginListeners += listener
        }

        @Override protected boolean processTree(rootNode, opaque) {
                boolean nodeProcessedByListener = false
                boolean retVal = true
                for (l in pluginListeners) {
                        PluginListenerResult res = l.process(rootNode, false, opaque)
                        if (res == PluginListenerResult.FAILED) {
                                retVal = false
                                break
                        }
                        nodeProcessedByListener = (res == PluginListenerResult.PROCESSED_STOP || res == PluginListenerResult.PROCESSED_CONTINUE)
                        if (res == PluginListenerResult.PROCESSED_STOP) {
                                break
                        }
                }
                if (retVal && !nodeProcessedByListener) {
                        retVal = processNode(rootNode, opaque)
                }
                if (retVal && rootNode.children.size()) {
                        retVal = processNodeBeforeChildrend(rootNode, opaque)
                        if (retVal) {
                                for (it in rootNode.children) {
                                        retVal = processTree(it, opaque)
                                        if (!retVal) {
                                                break
                                        }

                                }
                                if (retVal) {
                                        retVal = processNodeAfterChildrend(rootNode, opaque)
                                }
                        }
                }
                if (retVal && nodeProcessedByListener) {
                        for (l in pluginListeners) {
                                PluginListenerResult res = l.process(rootNode, true, opaque)
                                if (res == PluginListenerResult.FAILED) {
                                        retVal = false
                                        break
                                }
                                if (res == PluginListenerResult.PROCESSED_STOP) {
                                        break
                                }
                        }
                }
                return retVal
        }
}