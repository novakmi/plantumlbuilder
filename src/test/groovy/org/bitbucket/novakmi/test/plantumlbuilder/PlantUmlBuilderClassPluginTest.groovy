/*
Copyright (c) 2011 Michal Novak (bubbles.way@gmail.com)

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

package org.bitbucket.novakmi.test.plantumlbuilder

import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilder
import org.bitbucket.novakmi.plantumlbuilder.PlantUmlBuilderClassPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test

class PlantUmlBuilderClassPluginTest {
    @Test(groups = ["basic"])
    public void plantClassTest() {
        logger.trace("==> plantClassTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            pclass('MyClass')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
class MyClass
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()

        builder.plantuml {
            pclass('MyClass', stereotype: 'union')
        }
        Assert.assertEquals(builder.getBuiltText(),
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
        builder.addPlugin(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            pclass('MyClass', members: ['aaa', 'getAaa()', 'ccc'])
        }
        Assert.assertEquals(builder.getBuiltText(),
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
            pclass('MyClass', stereotype: 'project object', members: ['aaa', 'getAaa()', 'ccc'])
        }
        Assert.assertEquals(builder.getBuiltText(),
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
    public void plantAsTest() {
        logger.trace("==> plantAsTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            pclass('MyClass', as: 'My Class')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
class MyClass as "My Class"
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            penum('MyEnum', as: 'My Enum')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
enum MyEnum as "My Enum"
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            penum('MyEnum', as: 'MyEnum2')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
enum MyEnum as "MyEnum2"
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            pinterface('MyInterface', as: 'My Interface')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
interface MyInterface as "My Interface"
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder, 'as4')

        /*
        builder.reset()

        builder.plantuml {
            pclass('MyClass', stereotype: 'union')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
class MyClass << union >>
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)
        */
        logger.trace("<== plantAsTest")
    }


    @Test(groups = ["basic"])
    public void plantEnumTest() {
        logger.trace("==> plantEnumTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            penum('MyEnum')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
enum MyEnum
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()

        builder.plantuml {
            penum('MyEnum', stereotype: 'enum')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
enum MyEnum << enum >>
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantEnumTest")
    }

    @Test(groups = ["basic"])
    public void plantInterfaceTest() {
        logger.trace("==> plantInterfaceTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            pinterface('MyInterface')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
interface MyInterface
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()

        builder.plantuml {
            pinterface('MyInterface', stereotype: 'interface')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
interface MyInterface << interface >>
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)
        logger.trace("<== plantInterfaceTest")
    }

        @Test(groups = ["basic"])
        public void plantAbstractTest() {
                logger.trace("==> plantAbstractTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.addPlugin(new PlantUmlBuilderClassPlugin())

                builder.plantuml {
                        pabstract('MyAbstract')
                }
                Assert.assertEquals(builder.getBuiltText(),
                    '''@startuml
abstract MyAbstract
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()

                builder.plantuml {
                        pabstract('MyAbstract', stereotype: 'abstract')
                }
                Assert.assertEquals(builder.getBuiltText(),
                    '''@startuml
abstract MyAbstract << abstract >>
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)
                logger.trace("<== plantAbstractTest")
        }

        @Test(groups = ["basic"])
        public void plantAbstractClassTest() {
                logger.trace("==> plantAbstractClassTest")
                def builder = new PlantUmlBuilder() // new instance
                builder.addPlugin(new PlantUmlBuilderClassPlugin())

                builder.plantuml {
                        paclass('MyClassAbstract')
                }
                Assert.assertEquals(builder.getBuiltText(),
                    '''@startuml
abstract class MyClassAbstract
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)

                builder.reset()

                builder.plantuml {
                        paclass('MyClassAbstract', stereotype: 'abstract class')
                }
                Assert.assertEquals(builder.getBuiltText(),
                    '''@startuml
abstract class MyClassAbstract << abstract class >>
@enduml''')
                PlantUmlBuilderTest.assertPlantFile(builder)
                logger.trace("<== plantAbstractClassTest")
        }

    @Test(groups = ["basic"])
    public void plantPackageTest() {
        logger.trace("==> plantPackageTest")
        def builder = new PlantUmlBuilder() // new instance
        builder.addPlugin(new PlantUmlBuilderClassPlugin())

        builder.plantuml {
            ppackage('MyPackage')
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
package MyPackage
end package
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)

        builder.reset()
        builder.plantuml {
            ppackage('MyPackage') {
                pclass('PackageClass') {}
            }
        }
        Assert.assertEquals(builder.getBuiltText(),
            '''@startuml
package MyPackage
  class PackageClass
end package
@enduml''')
        PlantUmlBuilderTest.assertPlantFile(builder)


        logger.trace("<== plantPackageTest")
    }

    //Initialize logging
    private static final Logger logger = LoggerFactory.getLogger(PlantUmlBuilderClassPluginTest.class);
}
