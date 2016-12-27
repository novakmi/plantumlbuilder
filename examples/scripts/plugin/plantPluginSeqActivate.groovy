#!/usr/bin/env groovy
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)
//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8052')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '1.0.0')

u1 = 'User1'
m1 = 'MobileA'
m2 = 'MobileB'
u2 = 'User2'

uml = {
        title "Plantuml builder seq plugin example with activation colors and boxes"

        box {
                actor u1
        }
        box "Mobile", {
                participant m1
        }
        box "Mobile", color: "#LightBlue", {
                participant m2
        }
        box color: "#green", {
                actor u2
        }

        msgAd u1, to: m1, text: "Start Call", returnText: "Call finished", {
                msg m1, to: m2, text: "Ring", noReturn: true
                msgAd u2, to: m2, activate: "#FFBBBB", text: "PickUp", returnText: "HangUp", {
                        msgAd m2, to: m1, activate: "#yellow", text: "Talk"
                }
        }
}

def builder = new PlantUmlBuilder() // new instance
builder.registerPlugin(new PlantUmlBuilderSeqPlugin())
fileName = "plant_plugin_seq_activate.png"
builder << {
        plantuml "${fileName}", {
                delegate << uml // apply uml closure
        }
}
println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image


