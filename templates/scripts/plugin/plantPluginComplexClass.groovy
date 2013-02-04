#!/usr/bin/env groovy
//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

import java.util.concurrent.TimeUnit

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux script './<scriptName>.groovy')
@GrabResolver(name = 'novakmirepo', root = 'https://github.com/novakmi/novakmirepo/raw/master/releases', m2compatible = true)
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '7500')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.5.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.3.1')

// Without Internet connection, run as groovy script with jars in the classpath (-cp), comment @Grab ... annotations above
// 'groovy -cp plantumlbuilder-x.x.x.jar:plantuml-xxxx.jar:nodebuilder-x.x.x.jar  <scriptName>.groovy'

// This script template represents example of usage with Class  plugin
def builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
builder.registerPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin()) // add class diagram plugin support
//name of file to generate
final def fileName = "plant_plugin_complex_class.png"
// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.

def phone = 'phone'
def phoneDevice = 'phone_device'
def fixed = 'fixed'
def mobile = 'mobile'
def brand = 'manufacturer'
def sim = 'sim'
def number = 'number'

builder.plantuml("${fileName}") {
        title('Plantuml builder plugin template')

        pinterface(phone, members: ['+ring()'], stereotype: "telecom")
        pabstract(phoneDevice, as: "phone device")  // you can also use paclass(phoneDevice, as: "phone device")
        pclass(number, as: "phone number", stereotype: "telecom")
        relation(phone, rel: '<|--', to: phoneDevice)

        ppackage("fixed\\nlines") { //use quotes and \\n to have new line
                pclass(fixed, as: "fixed phone", stereotype: "telecom")
                relation(phoneDevice, rel: '<|--', to: fixed)
        }

        note("Phone or Sim can have more\\nthan one number, number can be\\nassinged to one Phone/Sim only.",
            pos: "top of $number")
        relation(fixed, rel: '"*" o-- "1"', to: number)

        ppackage('"mobile lines"') { // use quotes to have space
                penum(brand, as: "mobile phone\\nmanufacturer",  // multiline, use double slash to escape first one
                    members: [
                        'nokia',
                        'samsung',
                        'sonyericsson',
                        'zte',
                    ])
                paclass(mobile, as: "mobile phone", members: ['#imei'], stereotype: 'mobile telecom') // you can also use pabstract(mobile, as: "mobile phone")
                pclass(sim, as: "sim card", members: ['#imsi', '+number'])
                relation(sim, rel: '"*" o-- "1"', to: number)
                relation(phoneDevice, rel: '<|--', to: mobile)
                relation(mobile, rel: '"1" o-- "*"', to: brand)
                note("$mobile can have 0 or more ${sim}s", pos: "bottom of $mobile")
                relation(mobile, rel: '"*" *-- "1"', to: sim)
        }

        // multiline note - currently supported as PlantUML statements only
        plant("note  as N") {
                plant('PlantUML classs diagram generated by\\nPlantumbuilder and Plantumlbuilder class plugin.') // indentation
                plant('All features of the class plugin are used in this diagram.') // indentation
                plant('http://plantuml.sourceforge.net/')
                plant('http://bitbucket.org/novakmi/plantumlbuilder')

        }
        plant('end note')

}

println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image
