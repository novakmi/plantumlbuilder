//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

class UmlClass {

        final static def name = "class" // groovy makes automatically getName(), setName()

        // when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
        final static def abstractList = 'AbstractList'
        final static def abstractCollection = 'AbstractCollection'
        final static def list = 'List'
        final static def collection = 'Collection'
        final static def timeUnits = 'TimeUnits'

        static def uml = {
                title "Plantuml class diagram template example"

                paclass abstractList
                pabstract abstractCollection
                pinterface list
                // e.g use pinterface instead of plant("interface $list"), members can also added pinterface(list, members: ['+get()', '+set()'])
                pinterface collection
                plant '' // empty line

                relation list, rel: '<|--', to: abstractList //can be used instead of plant("$list <|-- $abstractList")
                relation collection, rel: '<|--', to: abstractCollection
                plant ''

                relation collection, rel: '<|-', to: list
                relation abstractCollection, rel: '<|-', to: abstractList
                relation abstractList, rel: '<|--', to: java.util.ArrayList.class.name // use name of real class (we get automatically package)
                plant ''

                plant "${java.util.ArrayList.class.name} : ${java.lang.Object.class.name}[] elementData"
                plant "${java.util.ArrayList.class.name} : size()"

                penum timeUnits, members: ['DAYS', 'HOURS', 'MINUTES'] //for enums (classes, interfaces), declare members are list


                plant "note  as N", {
                        plant "class diagram (adapted from PlantUML documentation" // indentation
                        plant "http://plantuml.sourceforge.net/"
                }
                plant "end note"

        }
}
