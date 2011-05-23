package cz.atlas.bubbles.it.test.plantumlbuilder

import org.testng.annotations.Test
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilder
import org.testng.Assert
import org.slf4j.LoggerFactory
import org.slf4j.Logger

class PlantUmlBasicTest {
    @Test(groups = ["basic"])
    public void plantPlainTest() {
        logger.trace("==> plantPlainTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.plantuml {
            plant('A->B')
            plant('activate B')
            plant('B->C')
            plant('activate C')
            plant('C-->B')
            plant('deactivate C')
            plant('A-->B')
            plant('deactivate B')

        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
A->B
activate B
B->C
activate C
C-->B
deactivate C
A-->B
deactivate B
@enduml''')
        logger.trace("<== plantPlainTest")
    }


    @Test(groups = ["basic"])
    public void plantPlainIndentTest() {
        logger.trace("==> plantPlainIndentTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.plantuml {
            plant('A->B')
            plant('activate B') {
                plant('B->C')
                plant('activate C')
                plant('C-->B')
                plant('deactivate C')
            }
            plant('A-->B')
            plant('deactivate B')

        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
A->B
activate B
  B->C
  activate C
  C-->B
  deactivate C
A-->B
deactivate B
@enduml''')
        logger.trace("<== plantPlainIndentTest")
    }
    /** Initialize logging                                                                        */
    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBasicTest.class);

}

