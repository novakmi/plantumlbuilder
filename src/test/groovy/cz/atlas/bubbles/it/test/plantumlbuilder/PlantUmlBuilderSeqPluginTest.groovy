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

package cz.atlas.bubbles.it.test.plantumlbuilder

import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilder
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilderSeqPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import org.testng.Assert

class PlantUmlBuilderSeqPluginTest {

    @Test(groups = ["basic"])
    public void plantSeqDividerDelayTest() {
        logger.trace("==> plantSeqDividerDelayTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderSeqPlugin())

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
        builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderSeqPlugin())
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
        PlantUmlBuilderTest.assertPlantFile(builder)

        logger.trace("<== plantSeqActivateTest")
    }
    //Initialize logging
    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBuilderSeqPluginTest.class);
}
