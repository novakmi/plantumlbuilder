#!/usr/bin/env groovy

import java.util.concurrent.TimeUnit

/*
Copyright (c) 2011 Michal Novak (bubbles.way@gmail.com)
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
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '7497')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.1.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '0.2.1')

// Without Internet connection, run as groovy script with jars in the classpath (-cp), comment @Grab ... annotations above
// 'groovy -cp plantumlbuilder-0.1.0.jar:plantuml-7497.jar:nodebuilder-0.0.2.jar  <scriptName>.groovy'

// This script template represents example of usage with Class  plugin
def builder = new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder() // create new builder
builder.addPlugin(new org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin()) // add class diagram plugin support
//name of file to generate
final def fileName = "plant_plugin_complex_class.png"
// when variables are used instead of direct strings, one can benefit from IDE auto-completion, refactoring, etc.

def phone = 'phone'
def phoneDevice = 'phone_device'
def fixed = 'fixed'
def mobile = 'mobile'
def classic = 'classic_mobile'
def smartphone = 'smartphone'
def brand = 'manufacturer'
def sim = 'sim'
def number = 'number'

builder.plantuml("${fileName}") {
        title('Plantuml builder plugin template')

        pinterface(phone, members: ['+ring()'])
        pabstract(phoneDevice, as: "phone device")  // you can also use paclass(phoneDevice, as: "phone device")
        pclass(number, as: "phone number")
        relation(phone, rel: '<|--', to: phoneDevice)

        ppackage("fixed\\nlines") { //use quotes and \\n to have new line
                pclass(fixed, as: "fixed phone")
                relation(phoneDevice, rel: '<|--', to: fixed)
        }

        note("Phone or Sim can have more\\nthan one number,\\nnumber can be\\nassinged to one Phone/Sim only.",
            pos: "top of $number")
        relation(fixed, rel: '"*" o-- "1"', to: number)

        ppackage('"mobile lines"') { // use quotes to have space
                penum(brand, as: "mobile phone\\nmanufacturer",  // multi line, use double slasdh to escape first one
                    members: [
                        'nokia',
                        'samsung',
                        'sonyericsson',
                        'zte',
                    ])
                paclass(mobile, as: "mobile phone", members: ['#imei']) // you can also use pabstract(mobile, as: "mobile phone")
                pclass(sim, as: "sim card", members: ['#imsi', '+number'])
                relation(sim, rel: '"*" o-- "1"', to: number)
                relation(phoneDevice, rel: '<|--', to: mobile)
                relation(mobile, rel: '"1" o-- "*"', to: brand)
                //abstract()
        }

        /*
        plant("note  as N") {
                plant('class diagram (adapted from PlantUML documentation') // indentation
                plant('http://plantuml.sourceforge.net/)')
        }
        plant('end note')
        */
}

println builder.getText() // get and print PlantUML text
new net.sourceforge.plantuml.SourceStringReader(builder.getText()).generateImage(new FileOutputStream("./${fileName}")) // create image
