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
 * Builder SimpleNode
 */
class SimpleNode {
        def name = ''   // name of the node (e.g myNode('text, at:'attrib') - name is 'myNode')
        def parent = null // parent node (or null if this is root node)
        def attributes = [:] //attributes map (e.g. myNode('text, at:'atrib') - attributes.at == 'attrib')
        def value = null //value of node (e.g. myNode('text, at:'attrib') - value is text)
        def children = [] //children nodes
}

abstract class SimpleNodeBuilder extends BuilderSupport {

        SimpleNode root = null //root node of the model

        @Override protected void setParent(Object parent, Object child) {
                ((SimpleNode) parent).children.add(child)
                ((SimpleNode) child).parent = parent
                if (!root) root = parent
        }

        @Override protected Object createNode(Object name) {
                return new SimpleNode(name: name)
        }

        @Override protected Object createNode(Object name, Object value) {
                return new SimpleNode(name: name, value: value)
        }

        @Override protected Object createNode(Object name, Map attributes) {
                return new SimpleNode(name: name, attributes: attributes)
        }

        @Override protected Object createNode(Object name, Map attributes, Object value) {
                return new SimpleNode(name: name, value: value, attributes: attributes)
        }

        /**
         * Process node.
         * Default implementation does not do anything.
         * This method can be overridden in derived implementation.
         * @param node to be processed
         * @param opaque opaque object to be passed for node processing
         * @return true ... ok, false .. failure
         */
        protected boolean processNode(SimpleNode node, Object opaque) {
                return true
        }

        /**
         * Process node before children nodes are processed.
         * Method is called after 'processNode' and only if node has children nodes. It can be used to
         * indent output, add opening bracket, etc.
         * Default implementation does not do anything.
         * This method can be overridden in derived implementation.
         * @param node to be processed
         * @param opaque opaque object to be passed for node processing
         * @return true ... ok, false .. failure
         */
        protected boolean processNodeBeforeChildrend(SimpleNode node, Object opaque) {
                return true
        }

        /**
         * Process node after children nodes are processed.
         * Method is called after 'processNode' and only if node has children nodes. It can be used to
         * indent output, add closing bracket, etc.
         * Default implementation does not do anything.
         * This method can be overridden in derived implementation.
         * @param node to be processed
         * @param opaque opaque object to be passed for node processing
         * @return true ... ok, false .. failure
         */
        protected boolean processNodeAfterChildrend(SimpleNode node, Object opaque) {
                return true
        }

        /**
         * Process node and its children.
         * This method can be overridden in derived implementation.
         * @param node node to be processed
         * @param node opaque object to be passed for node processing
         * @param tree opaque object to be passed for tree processing (recursion)
         * @return true ... ok, false .. failure
         */
        protected boolean processNodeWithChildren(SimpleNode node, Object nodeOpaque = null, Object treeOpaque = null) {
                boolean retVal = true
                if (retVal && node.children.size()) {
                        retVal = processNodeBeforeChildrend(node, nodeOpaque)
                        if (retVal) {
                                for (it in node.children) {
                                        retVal = processTree(it, nodeOpaque, treeOpaque)
                                        if (!retVal) {
                                                break
                                        }

                                }
                                if (retVal) {
                                        retVal = processNodeAfterChildrend(node, nodeOpaque)
                                }
                        }
                }
                return retVal
        }

        /**
         * Process tree of nodes
         * This method can be overridden in derived implementation.
         * @param rootNode root node of the tree (can be also leaf)
         * @param node opaque object to be passed for node processing
         * @param tree opaque object to be passed for tree processing (recursion)
         * @return true ... ok, false .. failure
         */
        protected boolean processTree(SimpleNode rootNode, Object nodeOpaque = null, Object treeOpaque = null) {
                boolean retVal = true
                retVal = processNode(rootNode, nodeOpaque)
                if (retVal) {
                        retVal = processNodeWithChildren(rootNode, nodeOpaque, treeOpaque)
                }
                return retVal
        }

        /**
         * Reset root element of the builder.
         */
        public void reset() {
                root = null
        }
}
