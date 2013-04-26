//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.plantumlbuilder

import groovy.util.logging.Slf4j
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderCompPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test


@Slf4j //Initialize logging
class PlantUmlBuilderCompPluginTest {
        @Test(groups = ["basic"])
        public void plantGroupingCompTest() {
                log.trace("==> plantGroupingCompTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderCompPlugin())

                builder.plantuml {
                        'package'()
                        'package'('My Package')
                }
                Assert.assertEquals(builder.getText(),
                        '''@startuml
package {
}
package "My Package" {
}
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                //builder.reset()
                log.trace("<== plantGroupingCompTest")
        }

        @Test(groups = ["basic"])
        public void plantIndentCompTest() {
                log.trace("==> plantIndentCompTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderCompPlugin())

                builder.plantuml {
                        'package'('My Package') {
                                node('My Node')
                        }
                }
                Assert.assertEquals(builder.getText(),
                        '''@startuml
package "My Package" {
  node "My Node" {
  }
}
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                log.trace("<== plantIndentCompTest")
        }

        @Test(groups = ["basic"])
        public void plantCompTest() {
                log.trace("==> plantCompTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderCompPlugin())

                builder.plantuml {
                        component('CompA')
                        component('My Component', as: 'CompB')
                        component('My Component printer', as: 'CompPrinter', stereotype: 'printer')

                }
                Assert.assertEquals(builder.getText(),
                        '''@startuml
component [CompA]
component [My Component] as CompB
component [My Component printer] as CompPrinter << printer >>
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                log.trace("<== plantCompTest")
        }

        @Test(groups = ["basic"])
        public void plantLinkCompTest() {
                log.trace("==> plantLinkCompTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.registerPlugin(new PlantUmlBuilderCompPlugin())

                builder.plantuml {
                        component('CompA')
                        component('CompB')
                        link('CompA', to: 'CompB')
                        link('CompB', to: 'CompA', type: '<..>')
                        link('CompA', to: 'CompB', description: 'my link')
                }
                Assert.assertEquals(builder.getText(),
                        '''@startuml
component [CompA]
component [CompB]
CompA --> CompB
CompB <..> CompA
CompA --> CompB : my link
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                log.trace("<== plantLinkCompTest")
        }

}
