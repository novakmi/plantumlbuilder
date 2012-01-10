/*
Copyright (c) 2012 Michal Novak (bubbles.way@gmail.com)
http://bitbucket.org/bubbles.way/plantumlbuilder

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

class UmlClass {

        static def name = "class" // groovy makes automatically getName(), setName()

        // when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
        final static def abstractList = 'AbstractList'
        final static def abstractCollection = 'AbstractCollection'
        final static def list = 'List'
        final static def collection = 'Collection'
        final static def timeUnits = 'TimeUnits'

        static def buildUml(builder) {
                builder.plantuml("${getName()}.png") {
                        title('Plantuml class diagram template example')

                        paclass(abstractList)
                        pabstract(abstractCollection)
                        pinterface(list)  // e.g use pinterface instead of plant("interface $list"), members can also added pinterface(list, members: ['+get()', '+set()'])
                        pinterface(collection)
                        plant('') // empty line

                        relation(list, rel: '<|--', to: abstractList) //can be used instead of plant("$list <|-- $abstractList")
                        relation(collection, rel: '<|--', to: abstractCollection)
                        plant('') // empty line

                        relation(collection, rel: '<|-', to: list)
                        relation(abstractCollection, rel: '<|-', to: abstractList)
                        relation(abstractList, rel: '<|--', to: java.util.ArrayList.class.name) // use name of real class (we get automatically package)
                        plant('') // empty line

                        plant("${java.util.ArrayList.class.name} : ${java.lang.Object.class.name}[] elementData")
                        plant("${java.util.ArrayList.class.name} : size()")

                        penum(timeUnits, members: ['DAYS', 'HOURS', 'MINUTES']) //for enums (classes, interfaces), declare members are list

                        plant("note  as N") {
                                plant('class diagram (adapted from PlantUML documentation') // indentation
                                plant('http://plantuml.sourceforge.net/)')
                        }
                        plant('end note')
                }

        }
}
