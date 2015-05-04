#!/usr/bin/env groovy
//import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

//@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8024')  //for newer versions, update numbers
//@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
//@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.4.3')

// Without Internet connection, run as groovy script with jars in the classpath (-cp), comment @Grab ... annotations above
// 'groovy -cp plantumlbuilder-x.x.x.jar:plantuml-xxxx.jar:nodebuilder-x.x.x.jar  <scriptName>.groovy'

// This script  represents examples from  http://plantuml.sourceforge.net/sequence.html
plugin =  new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin()
builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder(4, [plugin]) // create new builder

//name of file to generate
fileName = "plant_seq"
// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.
final def alice = 'Alice'
final def bob = 'Bob'
final def Foo = 'Foo'
i = 1

void finalize(imageAndReset = true) {
        println builder.getText() // get and print PlantUML text
        i++
        if (imageAndReset) {
                // create image
                new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}_${i}.png"))
                builder.reset()
        }
        println ""
}

builder.plantuml("Basic Example") {
        msg alice, to: bob, text: "Authentication Request", returnText: "Authentication Response"
        msg alice, to: bob , text: "Another authentication Request", returnText: "another authentication Response"
}
finalize()

builder.plantuml("Declaring participant") {
        def elems =  ["actor", "boundary", "control", "entity", "database"]
        elems.eachWithIndex{ p ,ix -> "$p" "$Foo${ix+1}"}
        elems[1..-1].eachWithIndex{ p ,ix -> msg "${Foo}1", to: "$Foo${ix+2}", text: "To ${elems[ix+1]}", noReturn: true}
}
finalize()

builder.plantuml("Declaring participant 2") {
        actor bob, color: "#red"
        participant alice
        participant '"I have a really\\nlong name"', as: "L", color: "#99FF99" //NOTE: in groovy use \\n, not \n
        msg alice, to: bob, text: "Authentication Request", noReturn: true
        msg bob, to: alice, text: "Authentication Response", noReturn: true
        msg bob, to: "L", text: "Log transaction", noReturn: true
}
finalize()

builder.plantuml("Message to Self") {
        msg alice, text: "This is a signal to self.\\nIt also demonstrates\\nmultiline \\ntext" //message to self has automatically noReturn: true
}
finalize()

builder.plantuml("Change arrow style") {
        ["-->x", "->", "->>", "-\\", "\\\\-", "//--", "->o", "o\\\\--", "<->", "<->o"].each { t ->  //NOTE: in groovy  use \\ for \
                msg bob, to: alice, noReturn: true, type: t
        }
}
finalize()

builder.plantuml("Change arrow color") {
        msg bob, to: alice, type: "-[#red]>", returnType: "-[#0000FF]->", text: "hello", returnText: "ok"
}
finalize()

// TODO autonumber

builder.plantuml("Tilte") {
        title "Simple communication example"
        msg alice, to: bob, text: "Authentication Request", returnText: "Authentication Response"
}
finalize()

//TODO legend

// TODO splitting  - newpage keyword
builder.plantuml("Splitting diagrams") {
        msg alice, to: bob, text: "message1", noReturn: true
        msg alice, to: bob, text: "message1", noReturn: true

        plant("newpage")

        msg alice, to: bob, text: "message2", noReturn: true
        msg alice, to: bob, text: "message3", noReturn: true

        plant("newpage A title for the\\nlast page")

        msg alice, to: bob, text: "message4", noReturn: true
        msg alice, to: bob, text: "message5", noReturn: true
}
finalize(false)
// this is example how to print splitted diagrams into image
net.sourceforge.plantuml.SourceStringReader s = new net.sourceforge.plantuml.SourceStringReader(builder.getText())
def cnt = s.getBlocks()[0].getDiagram().getNbImages() // count number of images (in first block) // TODO loop over all blocks?
def ret = true
for (int ix = 0; ix < cnt && ret != null; ix++) {
        FileOutputStream file = new FileOutputStream("./${fileName}_${i}${cnt > 1 ? "_${ix + 1}" : ''}.png")
        net.sourceforge.plantuml.FileFormatOption format = new net.sourceforge.plantuml.FileFormatOption(net.sourceforge.plantuml.FileFormat.PNG)
        if (cnt > 1) {
                ret = s.generateImage(file, ix, format)
        } else {
                ret = s.generateImage(file, format)
        }
        file.close()
}
builder.reset()

builder.plantuml("Grouping message") {
        msg alice, to: bob, text: "Authentication Request", noReturn: true
        alt "succesful case", {
                msg bob, to: alice, text: "Authentication Accepted", noReturn: true
                "else" "some kind of failure" // else is groovy keyword, so use ""
                msg bob, to: alice, text: "Authentication Failure", noReturn: true
                group "My own label", {
                        msg alice, to: "Log", text: "Log attack start", noReturn: true
                        loop("1000 times") {
                                msg alice, to: bob, text: "DNS attack", noReturn: true
                        }
                        msg alice, to: "Log", text: "Log attack end", noReturn: true
                }
                "else" "Another type of failure"
                msg bob, to: alice, text: "Please repeat", noReturn: true
        }
}
finalize()
