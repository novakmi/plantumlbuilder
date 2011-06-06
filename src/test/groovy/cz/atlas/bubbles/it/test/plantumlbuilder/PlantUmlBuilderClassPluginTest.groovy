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

package cz.atlas.bubbles.it.test.plantumlbuilder

import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilder
import cz.atlas.bubbles.it.plantumlbuilder.PlantUmlBuilderClassPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test

class PlantUmlBuilderClassPluginTest {
    @Test(groups = ["basic"])
    public void plantClassTest() {
        logger.trace("==> plantClassTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            plantclass('MyClass')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
class MyClass
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()

        builder.plantuml {
            plantclass('MyClass', stereotype: 'union')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
class MyClass << union >>
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantClassTest")
    }


    @Test(groups = ["basic"])
    public void plantClassMemberTest() {
        logger.trace("==> plantClassMemberTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            plantclass('MyClass', members: ['aaa', 'getAaa()', 'ccc'])
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
class MyClass {
  aaa
  getAaa()
  ccc
}
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()

        builder.plantuml {
            plantclass('MyClass', stereotype: 'project object', members: ['aaa', 'getAaa()', 'ccc'])
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
class MyClass << project object >> {
  aaa
  getAaa()
  ccc
}
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        logger.trace("<== plantClassMemberTest")
    }

    @Test(groups = ["basic"])
    public void plantEnumTest() {
        logger.trace("==> plantEnumTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlantUmlBuilderPluginListener(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            plantenum('MyEnum')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
enum MyEnum
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()

        builder.plantuml {
            plantenum('MyEnum', stereotype: 'enum')
        }
        Assert.assertEquals(builder.getText(),
            '''@startuml
enum MyEnum << enum >>
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantEnumTest")
    }

    //Initialize logging
    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBuilderClassPluginTest.class);
}
