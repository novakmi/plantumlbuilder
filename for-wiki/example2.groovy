//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/plantumlbuilder/src/LICENSE)

/*
Classpath to PlantUMLBuilder and plantuml.jar has to be set, e.g.:
groovy -cp ~/sw/PlantUml/plantuml.jar:../src/main/groovy/ example2.groovy
*/

import net.sourceforge.plantuml.SourceStringReader
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder

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
def builder = new PlantUmlBuilder()
// plantuml element is a root element of PlantUML
builder.plantuml {
    title('Plantumlbuilder example - scan')
    //create actors and participants
    actor('Joe', as: User1)
    actor('Sally', as: User2)
    participant('xsane' , as: scanSoftware)
    participant(scanner)
    participant('"Hard\\ndisk"', as: storage)
    scan(builder, User1, [paper, photo]) //call function - reuse for User1
    scan(builder, User2, [paper, paper]) //call function - reuse for user2
}

println builder.getText() // get PlantUML text
println ''

// use plantUML to create png file from plantuml tgext
SourceStringReader s = new SourceStringReader(builder.getText())
FileOutputStream file = new FileOutputStream('./example2.png')
s.generateImage(file)
file.close()
