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

/*
Use groovy 1.8.0+
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
