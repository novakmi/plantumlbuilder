/*
Copyright (c) 2011 Michal Novak (bubbles.way@gmail.com)

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

package org.bitbucket.novakmi.test.plantumlbuilder

import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import org.testng.Assert

class PlantUmlBuilderSeqPluginTest {

    @Test(groups = ["basic"])
    public void plantSeqDividerDelayTest() {
        logger.trace("==> plantSeqDividerDelayTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())

        builder.plantuml {
            actor('A')
            divider('my divider')
        }
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())
        builder.plantuml {
            actor('A')
            activate('A')
        }
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
actor A
activate A
**** close can be only 'deactivate' or 'destroy', not finish ***
@enduml''')

        PlantUmlBuilderTest.assertPlantFile(builder)

        logger.trace("<== plantSeqActivateTest")
    }
    //Initialize logging

    @Test(groups = ["basic"])
    public void plantSeqMsgTest() {
        logger.trace("==> plantSeqMsgTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
            Assert.assertEquals(builder.getBuiltText(),deactivateText)
            PlantUmlBuilderTest.assertPlantFile(builder)

            builder.reset()
            builder.plantuml {
                    makeParticipants()
                    msgAd(a, to: b, text: 'call b') //msgAd = activate/deactivate
            }
            Assert.assertEquals(builder.getBuiltText(),deactivateText)
            PlantUmlBuilderTest.assertPlantFile(builder)

            builder.reset()
        builder.plantuml {
            makeParticipants()
            msg(a, to: b, text: 'call b', returnText: 'return to a', close: 'deactivate')
        }
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
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
    public void plantSeqMsgNestingTest() {
        logger.trace("==> plantSeqMsgNestingTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())
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
        Assert.assertEquals(builder.getBuiltText(),
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
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())
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
            Assert.assertEquals(builder.getBuiltText(),
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
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())
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
        Assert.assertEquals(builder.getBuiltText(),
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
        builder.addPlugin(new PlantUmlBuilderSeqPlugin())
        def a = 'A'
        def b = 'B'
        builder.plantuml {
            builder.participant(a)
            builder.participant(b)
            ref('See diagram A', over: [a, b])
        }
        Assert.assertEquals(builder.getBuiltText(),
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
        Assert.assertEquals(builder.getBuiltText(),
            """@startuml
participant A
participant B
***** 'ref' requires 'over' attribute ****
@enduml""")
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantSeqRefTest")
    }

    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBuilderSeqPluginTest.class);
}
