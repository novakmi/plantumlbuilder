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

builder.plantuml("Message sequence numbering") {
        autonumber()
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
}
finalize()

builder.plantuml("Message sequence numbering 2") {
        autonumber()
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
        autonumber(15)
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
        autonumber(40, step: 10)
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
}
finalize()

builder.plantuml("Message sequence numbering 3") {
        autonumber(format:"<b>[000]")
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
        autonumber(15, format: "<b>(<u>##</u>)")
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
        autonumber(40, step: 10, format: "<font color=red><b>Message 0  ")
        msg bob, to: alice, text: "Authentication Request", returnText: "Authentication Response"
}
finalize()

builder.plantuml("Tilte") {
        title "Simple communication example"
        msg alice, to: bob, text: "Authentication Request", returnText: "Authentication Response"
}
finalize()

//TODO legend

builder.plantuml("Splitting diagrams") {
        msg alice, to: bob, text: "message1", noReturn: true
        msg alice, to: bob, text: "message1", noReturn: true

        newpage()

        msg alice, to: bob, text: "message2", noReturn: true
        msg alice, to: bob, text: "message3", noReturn: true

        newpage "A title for the\\nlast page"

        msg alice, to: bob, text: "message4", noReturn: true
        msg alice, to: bob, text: "message5", noReturn: true
}
finalize(false)
// this is example how to print splitted diagrams into images
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

builder.plantuml("Notes on messages") {
        msg alice, to: bob, text: "Hello", noReturn: true
        note "this is a first note", pos: "left"
        msg bob, to: alice, text: "ok", noReturn: true
        msg bob, text: "I am thinking"
        note "this is another note", pos: "right"
        note "a note\\ncan also be defined\\non several lines", pos: "left" //TODO no block note support yet
}
finalize()

builder.plantuml("Some other notes") {
        participant alice
        participant bob
        note "This is displayed\\nleft of ${alice}.", pos: "left of ${alice} #aqua" //TODO better color handling
        note "This is displayed right of ${alice}.", pos: "right of ${alice}"
        note "This is displayed over ${alice}.", pos: "over ${alice}"
        note "This is displayed\\n  over ${bob} and ${alice}.", pos: "over ${bob},${alice} #FFAAAA"
        note "This is yet another\\nexample of\\na long note.", pos: "over ${bob},${alice}"
}
finalize()

builder.plantuml("Changing note shapes") {
        msg "caller", to: "server", text: "conReq", returnType: "->", returnText: "conConf", {
                hnote "idle", pos: "over caller" //notice how to nest elements
        }
        rnote '"r" as rectangle\\n"h" as hexagon', pos: "over server"
}
finalize()

// TODO Creole and HTML

builder.plantuml("Divider") {
        divider "Initialization"
        msg alice, to: bob, text: "Authentication Request", returnText: "Authentication Response"
        divider "Repetition"
        msg alice, to: bob, text: "Another authentication Request", returnText: "Another authentication Response"
}
finalize()

builder.plantuml("Reference") {
        participant alice
        actor bob
        ref "init", over: [alice, bob]  // NOTE: use array for over
        msg alice, to: bob, text: "hello", noReturn: true
        ref "This can be on\\nseveral lines", over: [bob]
}
finalize()

builder.plantuml("Delay") {
        msg alice, to: bob, text: "Authentication Request", returnText: "Authentication Response", {
                delay ""
        }
        delay "5 minutes latter"
        msg bob, to: alice, text: "bye", noReturn: true
}
finalize()

builder.plantuml("Lifeline Activation and Destruction") {
        def user = "User"
        participant user
        msgAd user, to: "A", text: "doWork", returnText: "Done", {  // se how activate/deactivate is automatically added when block is finished
                msgAd "A", to: "B", text: "<< createRequest >>", returnText: "RequestCreated", {
                        msgAd "B", to: "C", text: "DoWork", returnText: "WorkDone", close: "destroy"
                }
        }
}
finalize()

builder.plantuml("Lifeline Activation and Destruction with colors") {
        def user = "User"
        participant user
        msgAd user, to: "A", text: "doWork", returnText: "Done", activate: "#FFBBBB", {  // color is specified as value of 'activate'
                msgAd "A", text: "Internal call", activate: "#DarkSalmon", {
                        msgAd "A", to: "B", text: "<< createRequest >>", returnText: "RequestCreated"
                }
        }
}
finalize()

builder.plantuml("Participant creation") {
        def other = "Other"
        def string = "String"
        msg alice, to: bob, text: "Hello", returnText: "ok", {
                create other
                msg alice, to: other, text: "new", noReturn: true
                create string, type: "control"
                msg alice, to: string, noReturn: true
                note "You can also put notes!", pos: "right"
        }
}
finalize()

builder.plantuml("Incoming and outgoing messages") {
        msgAd "[", to: "A", text: "doWork", returnText: "Done", {  // se how activate/deactivate is automatically added when block is finished
                msgAd "A", text: "Internal call", {
                        msg "]", to: "A", text: "<< createRequest >>", returnText:  "RequestCreated"
                }
        }
}
finalize()

builder.plantuml("Participants encompass") {
        def other = "Other"

        box "Internal Service", color: "#LightBlue", {
                participant bob
                participant alice
        }
        msg alice, to: bob, text: "hello", noReturn: true
        msg alice, to: other, text: "hello", noReturn: true

}
finalize()

builder.plantuml("Removing Footer") {
        plant "hide footbox" // use plant //TODO
        title "Footer removed"
        msg alice, to: bob, text: "Authentication Request", returnText: "Authentication Response"
}
finalize()