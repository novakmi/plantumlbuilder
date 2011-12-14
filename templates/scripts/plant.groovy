#!/usr/bin/env groovy

/*
Copyright (c) 2011 Micvhal Novak (bubbles.way@gmail.com)
http://bitbucket.org/bubbles.way/plantumlbuilder

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

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy plant.groovy' (or as Linux script './plant.groovy')
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '7497')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.1.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.2.0')

// Without Internet connection, run as groovy script with jars in classpath (-cp), comment @Grab ... above
// 'groovy -cp plantumlbuilder-0.1.0.jar:plantuml-7497.jar:nodebuilder-0.0.2.jar  plant.groovy'

// This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder

// when variables are used instead of strings, one can benefit from IDE auto-completion
final def A = 'A'
final def B = 'B'
final def C = 'C'

builder.plantuml {
        // example of block reuse
        def interact = {a, b ->
                plant("$a->$b")
                plant("activate $b")
                plant("$a-->$b")
                plant("deactivate $b")
        }

        participant('x', as: A )
        participant(B)
        participant(C)
        plant("$A->$B")
        plant("activate $B") {
                interact(B, C) //reuse
        }
        plant("$A-->$B")
        plant("deactivate $B")
        interact(C, B) //reuse
}

println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream('./plant.png')) // create image
