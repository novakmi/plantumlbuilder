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

import org.testng.annotations.Test
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilder
import org.testng.Assert
import org.slf4j.LoggerFactory
import org.slf4j.Logger

class PlantUmlBasicTest {

    /**
     * Helper method to create basic indented seq. diagram.
     * @param builder
     */
    private static def _buildSeq(builder) {
        builder.plant('A->B')
        builder.plant('activate B') {
            plant('B->C')
            plant('activate C')
            plant('C-->B')
            plant('deactivate C')
        }
        builder.plant('A-->B')
        builder.plant('deactivate B')
    }

    /**
     * Helper string constatnt to verify basic indented diagram.
     * @see PlantUmlBasicTest._buildSeq
     */
    private final def _seqStringNoNL = '''A->B
activate B
  B->C
  activate C
  C-->B
  deactivate C
A-->B
deactivate B'''

    private final def _seqString = "\n${_seqStringNoNL}\n"
    /**
     * Basic test to create non indented sequence diagram.
     */
    @Test(groups = ["basic"])
    public void plantPlainSeqTest() {
        logger.trace("==> plantPlainSeqTest")
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
        logger.trace("<== plantPlainSeqTest")
    }

    /**
     * Basic test to create indented sequence diagram.
     */
    @Test(groups = ["basic"])
    public void plantPlainSeqIndentTest() {
        logger.trace("==> plantPlainSeqIndentTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.plantuml {
            _buildSeq(builder)
        }
        Assert.assertEquals(builder.getText(), "@startuml${_seqString}@enduml")
        logger.trace("<== plantPlainSeqIndentTest")
    }

    /**
     * Basic test to test builder reset fucntion.
     */
    @Test(groups = ["basic"])
    public void plantPlainResetTest() {
        logger.trace("==> plantPlainResetTest")
        def builder = new PlantUmlBuilder() // new instance

        builder.plantuml {
            _buildSeq(builder)
        }
        Assert.assertEquals(builder.getText(), "@startuml${_seqString}@enduml")
        builder.reset()
        Assert.assertEquals(builder.getText(), '@startuml\n@enduml')
        builder.plantuml {
            _buildSeq(builder)
        }
        Assert.assertEquals(builder.getText(), "@startuml${_seqString}@enduml")
        logger.trace("<== plantPlainResetTest")
    }


    @Test(groups = ["basic"])
    public void plantPlainGetTextParamsTest() {
        logger.trace("==> plantPlainGetTextParamsTest")
        def builder = new PlantUmlBuilder() // new instance

        builder.plantuml {
            _buildSeq(builder)
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), "${_seqStringNoNL}\n")
        builder.reset()
        builder.plantuml {
            _buildSeq(builder)
        }
        Assert.assertEquals(builder.getText(), "@startuml${_seqString}@enduml")
        logger.trace("<== plantPlainGetTextParamsTest")
    }
    /**
     * Basic test to test builder reset fucntion.
     */
    @Test(groups = ["basic"])
    public void plantPlainKeywordsTest() {
        logger.trace("==> plantPlainKeywordsTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.plantuml {
            title('Test title')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'title Test title\n')
        builder.reset()
        builder.plantuml {
            actor('MyActor')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'actor MyActor\n')
        builder.reset()
        builder.plantuml {
            actor('MyActor', text: 'My actor\\n(system)')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'actor My actor\\n(system) as MyActor\n')
        logger.trace("<== plantPlainKeywordsTest")
    }
    /** Initialize logging                                                                                   */
    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBasicTest.class);

}

