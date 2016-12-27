#Plantumlbuilder

Plantumlbuilder is a groovy builder for the PlantUML (see [PlantUML][plantuml_id]).

See also `changelog.md`.

It is used to create the `PlantUML` diagrams with `groovy` language, which adds  
support for programming language and reuse. It also helps to keep logical indentation 
of the `PlantUML` structure (e.g. in the sequence diagrams). 

Implementation source code is in the `main/src` directory.  
Test source code is in  the `main/tests` directory.  
Examples are in the `templates` directory.  
Documentation files and source code is in the  `documentation` directory.  

Use [gradle][gradle_id] to build, test and package project.
Use [groovy][groovy_id] version 2.2.0 and later (recommended is 2.4.0 and later).

See/run scripts in the `templates/scripts/plantuml/` directory to generate examples from the `PlantUML` web page.   
(NOTE: Currently only examples for the sequence diagrams).

HTML documentation is located at:    
http://bitbucket.org/novakmi/plantumlbuilder/downloads

With the `plantumlbuilder` any `PlantUML` diagram can be created/programmed with use of `plant` keyword.  
With use of plugins (e.g. `PlantUmlBuilderSeqPlugin`) additional syntax (for specific diagram type) can be added.  

**Example**:

`groovy` `plantumlbuilder` code

    builder.plantuml("Lifeline Activation and Destruction with colors") {
            def user = "User"
            participant user
            msgAd user, to: "A", text: "doWork", returnText: "Done", activate: "#FFBBBB", {  // color is specified as value of 'activate'
                    msgAd "A", text: "Internal call", activate: "#DarkSalmon", {
                            msgAd "A", to: "B", text: "<< createRequest >>", returnText: "RequestCreated"
                    }
            }
    }

generated `PlantUML` 

    @startuml Lifeline Activation and Destruction with colors
    participant User
    User -> A : doWork
    activate A #FFBBBB
        A -> A : Internal call
        activate A #DarkSalmon
            A -> B : << createRequest >>
            activate B
            B --> A : RequestCreated
            deactivate B
        deactivate A
    A --> User : Done
    deactivate A
    @enduml


## Build environment

`plantumlbuilder` and `nodebuilder` are accessible through JCenter maven repository

### Usage with `groovy` Grapes in script

```groovy
@Grab(group = 'net.sourceforge.plantuml', module = 'plantuml', version = '8052')  //for newer versions, update numbers
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'plantumlbuilder', version = '1.0.0')
...
```
Run `groovy` script as regular script (`Linux`) of with `groovy` command (`Linux`, `Windows`).

First run of the `groovy` scripts downloads dependencies into `~/.groovy/grapes` directory (Internet connection required),
next run of the script uses already downloaded dependencies (Internet connection not required).

`~/.groovy/grapes` can be moved to other development machine (Internet connection not required even for first run) of the script.

See  http://docs.groovy-lang.org/latest/html/documentation/grape.html

NOTE: With `groovy` version below `2.3.0` following dependency may also be needed:
```groovy
@Grab(group = 'org.codehaus.groovy', module = 'groovy-backports-compat23', version = '2.4.7')
```

### Usage with `gradle` build file

```groovy
dependencies {
        compile localGroovy()
        compile group: 'net.sourceforge.plantuml', name: 'plantuml', version: '8052'
        compile group: 'org.bitbucket.novakmi', name: 'nodebuilder', version: '1.0.0'
        compile group: 'org.bitbucket.novakmi', name: 'plantumlbuilder', version: '1.1.0'
}
...
```

### Usage with `groovy` and command line (without dependency on external repository)

Download desired (latest) version of the `nodebuilder` and `yangbuilder` jar files from  http://jcenter.bintray.com/org/bitbucket/novakmi/.  
Run with `groovy` command with classpath pointing to the downloaded `jar` files 
(e.g. `groovy -cp ./plantuml.jar:./nodebuilder.jar:./plantumlbuilder.jar plant_script.groovy`). 

[gradle_id]: http://www.gradle.org/  "Gradle"
[groovy_id]: http://groovy-lang.org/ "Groovy"


Michal Novak (<it.novakmi@gmail.com>)

[gradle_id]: http://www.gradle.org/  "Gradle"
[groovy_id]: http://www.groovy-lang.org/ "Groovy"
[plantuml_id]: http://plantuml.sourceforge.net/  "PlantUML"
