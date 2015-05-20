//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.plantumlbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test

class PlantUmlBuilderSeqPluginTest {

    @Test(groups = ["basic"])
    public void plantSeqDividerDelayTest() {
        logger.trace("==> plantSeqDividerDelayTest")
        def builder = new PlantUmlBuilder(2, [new PlantUmlBuilderSeqPlugin()]) // new instance, plugin(s) passed in constructor
        //builder.registerPlugin(new PlantUmlBuilderSeqPlugin()) // instead of calling registerPlugin

        builder.plantuml {
            actor('A')
            divider('my divider')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
== my divider ==
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            actor('A')
            delay('wait 5 minutes')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
...wait 5 minutes...
@enduml''')

        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantSeqDividerDelayTest")
    }

    @Test(groups = ["basic"])
    public void plantSeqActivateTest() {
        logger.trace("==> plantSeqActivateTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
        builder.plantuml {
            actor('A')
            activate('A')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
activate A
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            actor('A')
            activate('A')
            deactivate('A')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
activate A
deactivate A
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            actor('A')
            activate('A')
            destroy('A')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
activate A
destroy A
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            actor('A')
            activate('A', close: 'deactivate')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
activate A
deactivate A
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            actor('A')
            activate('A', close: 'destroy')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
activate A
destroy A
@enduml''')

            builder.reset()
            builder.plantuml {
                    actor('A')
                    activate('A', close: 'finish')
            }

            try {
                    builder.getText()
            } catch (BuilderException expected) {
                    Assert.assertTrue(expected.getMessage().contains("close can be only 'deactivate' or 'destroy', not finish"))
            }

    }
    //Initialize logging

    @Test(groups = ["basic"])
    public void plantSeqMsgTest() {
        logger.trace("==> plantSeqMsgTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
        def a = 'A'
        def b = 'B'
        def makeParticipants = {
            builder.actor(a)
            builder.participant(b)
        }
        builder.plantuml {
            makeParticipants()
            msg(a)
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
A -> A
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            makeParticipants()
            msg(a, text: 'self activate', close: 'deactivate')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
A -> A : self activate
activate A
deactivate A
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            makeParticipants()
            msg(a, to: b, text: 'call b', close: 'destroy')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
A -> B : call b
activate B
B --> A
destroy B
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

            def deactivateText = '''@startuml
actor A
participant B
A -> B : call b
activate B
B --> A
deactivate B
@enduml'''
            builder.reset()
            builder.plantuml {
                    makeParticipants()
                    msg(a, to: b, text: 'call b', close: 'deactivate')
            }
            Assert.assertEquals(builder.getText(),deactivateText)
            PlantUmlBuilderTest.assertPlantFile(builder)

            builder.reset()
            builder.plantuml {
                    makeParticipants()
                    msgAd(a, to: b, text: 'call b') //msgAd = activate/deactivate
            }
            Assert.assertEquals(builder.getText(),deactivateText)
            PlantUmlBuilderTest.assertPlantFile(builder)

            builder.reset()
        builder.plantuml {
            makeParticipants()
            msg(a, to: b, text: 'call b', returnText: 'return to a', close: 'deactivate')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
A -> B : call b
activate B
B --> A : return to a
deactivate B
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            makeParticipants()
            msg(a, to: b, type: '-->', text: 'call b', returnType: '->', returnText: 'return to a', close: 'deactivate')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
A --> B : call b
activate B
B -> A : return to a
deactivate B
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            makeParticipants()
            msg(a, to: b, type: '-->', text: 'call b', noReturn: true, activate: true)
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
A --> B : call b
activate B
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        logger.trace("<== plantSeqMsgTest")
    }

        @Test(groups = ["basic"])
        public void plantSeqMsgActivateValueTest() {
                logger.trace("==> plantSeqMsgActivateValueTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
                def a = 'A'
                def b = 'B'
                def makeParticipants = {
                        builder.actor(a)
                        builder.participant(b)
                }

                builder.reset()
                builder.plantuml {
                        makeParticipants()
                        msg(a, text: 'self activate', activate: "#FFBBBB", close: 'deactivate')
                }
                Assert.assertEquals(builder.getText(),
                        '''@startuml
actor A
participant B
A -> A : self activate
activate A #FFBBBB
deactivate A
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()
                builder.plantuml {
                        makeParticipants()
                        msgAd(a, to: b, activate: "#FFBBBB")
                }
                Assert.assertEquals(builder.getText(),
                        '''@startuml
actor A
participant B
A -> B
activate B #FFBBBB
B --> A
deactivate B
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)


                logger.trace("<== plantSeqMsgActivateValueTest")
        }


    @Test(groups = ["basic"])
    public void plantSeqMsgNestingTest() {
        logger.trace("==> plantSeqMsgNestingTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
        def a = 'A'
        def b = 'B'
        def c = 'C'
        def makeParticipants = {
            builder.actor(a)
            builder.participant(b)
            builder.participant(c)
        }
        builder.plantuml {
            makeParticipants()
            msg(a, to: b, close: 'deactivate', text: 'call b', returnText: 'operation finished') {
                msg(b, to: c, close: 'destroy', text: 'call c') {
                    msg(c, text: 'self c')
                    msg(c, to: b, text: 'call b, no return', noReturn: true)
                }
            }
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
actor A
participant B
participant C
A -> B : call b
activate B
  B -> C : call c
  activate C
    C -> C : self c
    C -> B : call b, no return
  C --> B
  destroy C
B --> A : operation finished
deactivate B
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantSeqMsgNestingTest")
    }

    @Test(groups = ["basic"])
    public void plantSeqGroupTest() {
        logger.trace("==> plantSeqGroupTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
        def a = 'A'
        def b = 'B'
        ['group', 'critical', 'opt', 'loop', 'par', 'break'].each { gr ->
            builder.reset()
            builder.plantuml {
                builder.participant(a)
                builder.participant(b)
                "$gr"("${gr}Text") {
                    msg(a, to: b, close: 'deactivate', text: 'call b', returnText: 'operation finished')
                }
            }
            Assert.assertEquals(builder.getText(),
                """@startuml
participant A
participant B
${gr} ${gr}Text
  A -> B : call b
  activate B
  B --> A : operation finished
  deactivate B
end
@enduml""")
            PlantUmlBuilderTest.assertPlantFile(builder)
        }
        logger.trace("<== plantSeqGroupTest")
    }

    @Test(groups = ["basic"])
    public void plantSeqAltTest() {
        logger.trace("==> plantSeqAltTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
        def a = 'A'
        def b = 'B'
        builder.plantuml {
            builder.participant(a)
            builder.participant(b)
            alt('on case 1') {
                msg(a, to: b, close: 'deactivate', text: 'call b', returnText: 'operation finished')
                'else'('case 2')
                msg(b, to: a, close: 'destroy', text: 'call a', returnText: 'operation finished')
            }
        }
        Assert.assertEquals(builder.getText(),
            """@startuml
participant A
participant B
alt on case 1
  A -> B : call b
  activate B
  B --> A : operation finished
  deactivate B
  else case 2
  B -> A : call a
  activate A
  A --> B : operation finished
  destroy A
end
@enduml""")
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantSeqAltTest")
    }


    @Test(groups = ["basic"])
    public void plantSeqRefTest() {
        logger.trace("==> plantSeqRefTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
        def a = 'A'
        def b = 'B'
        builder.plantuml {
            builder.participant(a)
            builder.participant(b)
            ref('See diagram A', over: [a, b])
        }
        Assert.assertEquals(builder.getText(),
            """@startuml
participant A
participant B
ref over A,B : See diagram A
@enduml""")
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            builder.participant(a)
            builder.participant(b)
            ref('See diagram A')
        }
        try {
                builder.getText()
        } catch (BuilderException expected) {
                Assert.assertTrue(expected.getMessage().contains("'ref' requires 'over' attribute"))
        }
        logger.trace("<== plantSeqRefTest")
    }

        @Test(groups = ["basic"])
        public void plantSeqBoxTest() {
                logger.trace("==> plantSeqBoxTest")
                def a = 'A'
                def b = 'B'

                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
                builder.plantuml {
                        box {
                                participant(a)
                                participant(b)
                        }
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
box
  participant A
  participant B
end box
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()
                builder.plantuml {
                        box("BoxName") {
                                participant(a)
                                participant(b)
                        }
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
box "BoxName"
  participant A
  participant B
end box
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()
                builder.plantuml {
                        box("BoxName with space") {
                                participant(a)
                                participant(b)
                        }
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
box "BoxName with space"
  participant A
  participant B
end box
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()
                builder.plantuml {
                        box(color: "#LightBlue") {
                                participant(a)
                                participant(b)
                        }
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
box #LightBlue
  participant A
  participant B
end box
@enduml""")

                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()
                builder.plantuml {
                        box("BoxName", color: "#LightBlue") {
                                participant(a)
                                participant(b)
                        }
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
box "BoxName" #LightBlue
  participant A
  participant B
end box
@enduml""")

                PlantUmlBuilderTest.assertPlantFile(builder)

                logger.trace("<== plantSeqBoxTest")
        }

        @Test(groups = ["basic"])
        public void plantSeqParticipantTest() {
                logger.trace("==> plantSeqAltTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
                def _participant = 'pA'
                def _actor = 'aA'
                def _boundary = 'bA'
                def _control = 'cA'
                def _entity = 'eA'
                def _database = 'dA'
                builder.plantuml {
                        participant(_participant)
                        participant(_participant + "C", color: "#red")
                        participant("\"Second ${_participant}\"", as: _participant+"2")
                        participant("\"Third ${_participant}\"", as: _participant+"3", stereotype: "database")
                        participant("\"Fourth ${_participant}\"", as: _participant+"4", stereotype: "database", color: "#green")
                        participant("\"Fifth ${_participant}\"", as: _participant+"5", stereotype: "(C,#ADD1B2) database", color: "#blue")

                        actor(_actor)
                        actor(_actor + "C", color: "#red")
                        actor("\"Second ${_actor}\"", as: _actor+"2")
                        actor("\"Third ${_actor}\"", as: _actor+"3", stereotype: "actor")
                        actor("\"Fourth ${_actor}\"", as: _actor+"4", stereotype: "actor", color: "#green")

                        boundary(_boundary)
                        boundary(_boundary+"C", color: "#red")
                        boundary("\"Second ${_boundary}\"", as: _boundary+"2")
                        boundary("\"Third ${_boundary}\"", as: _boundary+"3", stereotype: "boundary")
                        boundary("\"Fourth ${_boundary}\"", as: _boundary+"4", stereotype: "boundary", color: "#green")

                        control(_control)
                        control(_control+"C", color: "#red")
                        control("\"Second ${_control}\"", as: _control+"2")
                        control("\"Third ${_control}\"", as: _control+"3", stereotype: "control")
                        control("\"Fourth ${_control}\"", as: _control+"4", stereotype: "control", color: "#green")

                        entity(_entity)
                        entity(_entity+"C", color: "#red")
                        entity("\"Second ${_entity}\"", as: _entity+"2")
                        entity("\"Third ${_entity}\"", as: _entity+"3", stereotype: "entity")
                        entity("\"Fourth ${_entity}\"", as: _entity+"4", stereotype: "entity", color: "#green")

                        database(_database)
                        database(_database+"C", color: "#red")
                        database("\"Second ${_database}\"", as: _database+"2")
                        database("\"Third ${_database}\"", as: _database+"3", stereotype: "database")
                        database("\"Fourth ${_database}\"", as: _database+"4", stereotype: "database", color: "#green")
                        database("\"Fifth ${_database}\"", as: _database+"5", stereotype: "(D,#ADD1B2) database", color: "#blue")
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
participant pA
participant pAC #red
participant "Second pA" as pA2
participant "Third pA" as pA3 <<database>>
participant "Fourth pA" as pA4 <<database>> #green
participant "Fifth pA" as pA5 <<(C,#ADD1B2) database>> #blue
actor aA
actor aAC #red
actor "Second aA" as aA2
actor "Third aA" as aA3 <<actor>>
actor "Fourth aA" as aA4 <<actor>> #green
boundary bA
boundary bAC #red
boundary "Second bA" as bA2
boundary "Third bA" as bA3 <<boundary>>
boundary "Fourth bA" as bA4 <<boundary>> #green
control cA
control cAC #red
control "Second cA" as cA2
control "Third cA" as cA3 <<control>>
control "Fourth cA" as cA4 <<control>> #green
entity eA
entity eAC #red
entity "Second eA" as eA2
entity "Third eA" as eA3 <<entity>>
entity "Fourth eA" as eA4 <<entity>> #green
database dA
database dAC #red
database "Second dA" as dA2
database "Third dA" as dA3 <<database>>
database "Fourth dA" as dA4 <<database>> #green
database "Fifth dA" as dA5 <<(D,#ADD1B2) database>> #blue
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)
                logger.trace("<== plantSeqAltTest")
        }

        @Test(groups = ["basic"])
        public void plantSeqHnoteRnoteTest() {
                logger.trace("==> plantSeqHnoteRnoteTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
                def a = 'A'
                def b = 'B'
                builder.plantuml {
                        builder.participant(a)
                        note("See diagram A", pos: "over $a")
                        hnote("See diagram A", pos: "over $a")
                        rnote("See diagram A", pos: "over $a")
                        hnote("See\\ndiagram A", pos: "over $a")
                        rnote("See\\ndiagram A", pos: "over $a")
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
participant A
note over A : See diagram A
hnote over A : See diagram A
rnote over A : See diagram A
hnote over A : See\\ndiagram A
rnote over A : See\\ndiagram A
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)

                logger.trace("<== plantSeqHnoteRnoteTest")
        }

        @Test(groups = ["basic"])
        public void plantSeqInOutTest() {
                logger.trace("==> plantSeqInOutTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderSeqPlugin())

                def types = ["->", "o->", "o->o", "x->"]
                builder.plantuml {
                        msg("[", to: "A", noReturn: true)
                        msg("]", to: "A", noReturn: true)
                        types.each {t->
                                msg("[", to: "A", noReturn: true, type: t)
                                msg("]", to: "A", noReturn: true, type: t)
                        }
                        msg("[", to: "A", text: "i1", noReturn: true)
                        msg("]", to: "A", text: "o1", noReturn: true)
                        types.eachWithIndex { t, i ->
                                msg("[", to: "A", text: "ti${i}", noReturn: true, type: t)
                                msg("]", to: "A", text: "to${i}", noReturn: true, type: t)
                        }
                        msg("[", to: "A", text: "i2")
                        msg("]", to: "A", text: "o2")
                        types.eachWithIndex { t, i ->
                                msg("[", to: "A", text: "tri${i}", type: t)
                                msg("]", to: "A", text: "tro${i}", type: t)
                                msg("[", to: "A", text: "trri${i}", returnType: t)
                                msg("]", to: "A", text: "trro${i}", returnType: t)
                        }
                        msg("[", to: "A", text: "i3", returnText: "ri3")
                        msg("]", to: "A", text: "o3", returnText: "ro3")
                        msgAd("[", to: "A", text: "adi4")
                        msgAd("]", to: "A", text: "ado4")
                        types.eachWithIndex { t, i ->
                                msgAd("[", to: "A", text: "tadi${i}", type: t)
                                msgAd("]", to: "A", text: "tado${i}", type: t)
                        }
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
[-> A
A ->]
[-> A
A ->]
[o-> A
A o->]
[o->o A
A o->o]
[x-> A
A x->]
[-> A : i1
A ->] : o1
[-> A : ti0
A ->] : to0
[o-> A : ti1
A o->] : to1
[o->o A : ti2
A o->o] : to2
[x-> A : ti3
A x->] : to3
[-> A : i2
[<-- A
A ->] : o2
A <--]
[-> A : tri0
[<-- A
A ->] : tro0
A <--]
[-> A : trri0
[<- A
A ->] : trro0
A <-]
[o-> A : tri1
[<-- A
A o->] : tro1
A <--]
[-> A : trri1
[<-o A
A ->] : trro1
A <-o]
[o->o A : tri2
[<-- A
A o->o] : tro2
A <--]
[-> A : trri2
[o<-o A
A ->] : trro2
A o<-o]
[x-> A : tri3
[<-- A
A x->] : tro3
A <--]
[-> A : trri3
[<-x A
A ->] : trro3
A <-x]
[-> A : i3
[<-- A : ri3
A ->] : o3
A <--] : ro3
[-> A : adi4
activate A
[<-- A
deactivate A
A ->] : ado4
activate A
A <--]
deactivate A
[-> A : tadi0
activate A
[<-- A
deactivate A
A ->] : tado0
activate A
A <--]
deactivate A
[o-> A : tadi1
activate A
[<-- A
deactivate A
A o->] : tado1
activate A
A <--]
deactivate A
[o->o A : tadi2
activate A
[<-- A
deactivate A
A o->o] : tado2
activate A
A <--]
deactivate A
[x-> A : tadi3
activate A
[<-- A
deactivate A
A x->] : tado3
activate A
A <--]
deactivate A
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)

                logger.trace("<== plantSeqInOutTest")
        }


        @Test(groups = ["basic"])
        public void plantSeqAutonumberTest() {
                logger.trace("==> plantSeqAutonumberTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
                def a = 'A'
                def b = 'B'
                builder.plantuml {
                        autonumber()
                        msg a, to: b, text: "Request", returnText: "Response"
                        autonumber 15
                        msg a, to: b, text: "Request", returnText: "Response"
                        autonumber 40, step: 10
                        msg a, to: b, text: "Request", returnText: "Response"
                        autonumber format: "<b>[000]"
                        msg a, to: b, text: "Request", returnText: "Response"
                        autonumber 15, format: "<b>(<u>##</u>)"
                        msg a, to: b, text: "Request", returnText: "Response"
                        autonumber 40, step: 10, format: "<font color=red><b>Message 0"
                        msg a, to: b, text: "Request", returnText: "Response"
                }
                Assert.assertEquals(builder.getText(),
                        """@startuml
autonumber
A -> B : Request
B --> A : Response
autonumber 15
A -> B : Request
B --> A : Response
autonumber 40 10
A -> B : Request
B --> A : Response
autonumber "<b>[000]"
A -> B : Request
B --> A : Response
autonumber 15 "<b>(<u>##</u>)"
A -> B : Request
B --> A : Response
autonumber 40 10 "<font color=red><b>Message 0"
A -> B : Request
B --> A : Response
@enduml""")
                PlantUmlBuilderTest.assertPlantFile(builder)

                logger.trace("<== plantSeqAutonumberTest")
        }

        private static final Logger logger = LoggerFactory.getLogger(PlantUmlBuilderSeqPluginTest.class);
}
