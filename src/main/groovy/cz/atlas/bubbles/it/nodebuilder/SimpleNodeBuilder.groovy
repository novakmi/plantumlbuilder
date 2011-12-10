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

import sun.java2d.pipe.SpanShapeRenderer.Simple

/**
 * Builder node
 */
class SimpleNode {
        def name = ''   // name of node (e.g leaf('text, at:'attrib') - name is 'leaf')
        def parent = null // parent node (or null if this is root node)
        def attributes = [:] //attributes map (e.g. leaf('text, at:'atrib') - attributes.at == 'attrib')
        def value = null //value of node (e.g. leaf('text, at:'attrib') - value is text)
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

        abstract protected boolean processNode(SimpleNode node, opaque)
        abstract protected boolean processNodeAfterChildrend(SimpleNode node, opaque)
        abstract protected boolean processNodeBeforeChildrend(SimpleNode node, opaque)

        protected boolean processTree(rootNode, opaque) {
                boolean retVal = true
                retVal = processNode(rootNode, opaque)
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
                return retVal
        }

        /**
         * Reset root element of the builder.
         */
        public void reset() {
                root = null
        }
}
