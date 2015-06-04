//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.plantumlbuilder

import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import net.sourceforge.plantuml.SourceStringReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test

class PlantUmlBuilderTest {

    def final static makePlantFile = true

    static def assertPlantFile(builder, prefix = '') {
        logger.trace("==> assertPlantFile")
        if (makePlantFile) {
            def s = new SourceStringReader(builder.getText())
            logger.trace(builder.getText())
            def file = new FileOutputStream("./${prefix}_plantTestNg.png")
            s.generateImage(file);
            file.close()
        }
        logger.trace("<== assertPlantFile")
    }

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
 * Helper string constant to verify basic indented diagram.
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
        assertPlantFile(builder)
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
        assertPlantFile(builder)
        builder.reset()
        builder.plantuml('img/myseq.png') {
            _buildSeq(builder)
        }
        Assert.assertEquals(builder.getText(), "@startuml img/myseq.png${_seqString}@enduml")
        //assertPlantFile(builder)
        logger.trace("<== plantPlainSeqIndentTest")
    }

/**
 * Basic test to test builder reset function.
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
 * Basic test to test builder reset function.
 */
    @Test(groups = ["basic"])
    public void plantPlainKeywordsTest() {
        logger.trace("==> plantPlainKeywordsTest")
        def builder = new PlantUmlBuilder() // new instance

        // test 'title' keyword
        builder.plantuml {
            title('Test title')
            header('Test header')
            footer('Test footer')
            legend('Test legend')
            plant("participant A")
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true),
'''title Test title
header Test header
footer Test footer
legend
  Test legend
end legend
participant A
''')
        assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            title '''Test title
                     multiline'''
            header '''Test header
                     multiline'''
            footer '''Test footer
                     multiline'''
            legend '''Test legend
                     multiline'''
            plant("participant A")
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true),
                '''title
  Test title
  multiline
end title
header
  Test header
  multiline
end header
footer
  Test footer
  multiline
end footer
legend
  Test legend
  multiline
end legend
participant A
''')
        assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            legend "Test legend", pos: "left"
            plant("participant A")
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true),
                '''legend left
  Test legend
end legend
participant A
''')
        assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            legend '''Test legend
                multiline''', pos: "right"
            plant("participant A")
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true),
                '''legend right
  Test legend
  multiline
end legend
participant A
''')
        assertPlantFile(builder)

        // test 'actor' keyword
        builder.reset()
        builder.plantuml {
            plant('actor MyActor')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'actor MyActor\n')
        assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            plant('actor "My actor\\n(system)" as MyActor')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'actor "My actor\\n(system)" as MyActor\n')
        assertPlantFile(builder)

        // test 'participant'
        builder.reset()
        builder.plantuml {
            plant('participant MyParticipant')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'participant MyParticipant\n')
        assertPlantFile(builder)
        builder.reset()
        builder.plantuml {
            plant('participant "My participant\\n(system)" as MyParticipant')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'participant "My participant\\n(system)" as MyParticipant\n')
        assertPlantFile(builder)

        // test 'note' keyword
        builder.reset()
        builder.plantuml {
            plant('participant A')
            note('My note')
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), 'participant A\nnote My note\n')
        assertPlantFile(builder)
        ['right', 'left'].each {nt ->
            builder.reset()
            builder.plantuml {
                plant('participant A')
                note("My note $nt", pos: nt)
            }
            Assert.assertEquals(builder.getText(plainPlantUml: true), "participant A\nnote $nt : My note $nt\n")
            assertPlantFile(builder)
        }
        ['left', 'right', 'over', 'top', 'bottom'].each {nt ->
            builder.reset()
            builder.plantuml {
                plant('participant A')
                note("My note $nt A", pos: "$nt of A")
            }
            Assert.assertEquals(builder.getText(plainPlantUml: true), "participant A\nnote $nt of A : My note $nt A\n")
            assertPlantFile(builder)
        }
        // test note as
        builder.reset()
        builder.plantuml {
            note("This is my note", as: "N1")
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true), "note This is my note as N1\n")
        assertPlantFile(builder)
        // test note on multiple lines
        builder.reset()
        builder.plantuml {
            plant('participant A')
            note '''a note
                  can also be defined
                  on several lines''', pos: "left of A", color: "blue"

        }
        Assert.assertEquals(builder.getText(plainPlantUml: true),
            """participant A
note left of A #blue
  a note
  can also be defined
  on several lines
end note
""")
        assertPlantFile(builder)
        logger.trace("<== plantPlainKeywordsTest")
    }

    @Test(groups = ["basic"])
    public void plantNewpageTest() {
        logger.trace("==> plantNewpageTest")
        def builder = new PlantUmlBuilder() // new instance

        builder.plantuml {
            plant('participant A')
            newpage()
            plant('participant B')
            newpage "New page title"
            plant('participant C')
            newpage "New page title\\nmore lines"
        }
        Assert.assertEquals(builder.getText(plainPlantUml: true),
                """participant A
newpage
participant B
newpage New page title
participant C
newpage New page title\\nmore lines
""")
        assertPlantFile(builder)
        logger.trace("<== plantNewpageTest")
    }
    //Initialize logging
    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBuilderTest.class);
}

