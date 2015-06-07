#!/usr/bin/env groovy
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderSeqPlugin

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

import java.util.concurrent.TimeUnit

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@GrabResolver(name = 'novakmirepo', root = 'https://github.com/novakmi/novakmirepo/raw/master/releases', m2Compatible = true)
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8026')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.1')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.4.3')

def builder = new PlantUmlBuilder() // new instance
builder.registerPlugin(new PlantUmlBuilderSeqPlugin())

final def fileName = "plant_plugin_seq_activate.png"

def u1 = 'User1'
def m1 = 'MobileA'
def m2 = 'MobileB'
def u2 = 'User2'

builder.plantuml("${fileName}") {
        title 'Plantuml builder seq plugin template example with activation colors and boxes'

        box {
                actor u1
        }
        box "Mobile", {
                participant m1
        }
        box("Mobile", color: "#LightBlue") {
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

println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image


