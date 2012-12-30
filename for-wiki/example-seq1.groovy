//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/plantumlbuilder/src/LICENSE)

/*
Classpath to PlantUMLBuilder and plantuml.jar has to be set, e.g.:
groovy -cp ~/sw/PlantUml/plantuml.jar:blantumlbuilder-x.x.x.jar example-seq1.groovy
*/

import net.sourceforge.plantuml.SourceStringReader
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin

// define actors and participants as constant strings
User1 = 'User1'
User2 = 'User2'
scanner = 'scanner'
scanSoftware = 'scanSoftware'
storage = 'storage'

paper = 'paper'
photo = 'photo'


def scan(builder, user, mediaList) {
    mediaList.each {media ->
        builder.plant("$user -> $scanner: Insert $media into $scanner") {
            def scan = { // reuse via closure
                plant("$user -> $scanSoftware: Press 'Scan' button")
                plant("$scanSoftware -> $scanner: scan")
                plant("$scanner --> $scanSoftware: image data")
                plant("$user -> $scanSoftware: save")
                plant("$scanSoftware -> $storage: Create image file")
            }

            if (media == paper) {  // programming can be used
                plant("$user -> $scanSoftware: Set B/W 300 DPI")
                scan() // perform closure - reuse
            } else {
                plant("$user -> $scanSoftware: Set COLOR 1200 DPI")
                scan() // perform closure - reuse
            }
        }
        builder.plant("$user -> $scanner: Remove $media from $scanner")
    }
}
// create new builder
def builder = new PlantUmlBuilder() // new instance
        def seqPlugin =  new PlantUmlBuilderSeqPlugin()
        builder.registerPlugin(seqPlugin)
        def a = 'A'
        def b = 'B'
        builder.plantuml {
            title('Plantumlbuilder example sequence plugin/autonumber')
            participant(a)
            participant(b)
            alt('on case 1') {
                msg(a, to: b, close: 'deactivate', text: 'call b', returnText: 'operation finished')
                'else'('case 2')
                msg(b, to: a, close: 'destroy', text: 'call a', returnText: 'operation finished')
            }
        }


println builder.getText() // get PlantUML text
println ''

// use plantUML to create png file from plantuml tgext
SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream('./example-seq1.png')
s.generateImage(file)
file.close()
