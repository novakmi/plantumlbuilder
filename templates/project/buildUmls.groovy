#!/usr/bin/env groovy
/*
copyright (c) 2012 michal novak (bubbles.way@gmail.com)
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
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '7622')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.5.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.3.1')

//due to @Grab limitation in script, we have to have def ... after Grab, in our case we can just create builder
// see http://groovy.codehaus.org/Grapes+and+grab()
def builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
builder.registerPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin()) // add class diagram plugin support
builder.registerPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin()) // add seq. diagram plugin support

if (args) {  // arguments passed can be processed this way
        println "Following arguments have been passed:"
        args.each {a ->
                println(a)
        }
}

/**
 create List of classes that are related to generated uml files
 Each class has to have static function 'getName()' returning name of uml file (without uml extension) and
 static function  'buildUml' accepting empty builder as parameter
 */
def umls = [
    UmlClass,
    UmlSeq,
]

for (u in umls) { // one can also use umls.each {u->
        builder.reset() // empty builder
        u.buildUml(builder)
        print("Processing ${u.getName()} ...")
        new File("${u.getName()}.txt").write(builder.getText()) //write plantuml script to file
        new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${u.getName()}.png")) // create image
        println("done")
}


