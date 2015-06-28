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
Use [groovy][groovy_id] version 2.2.0 and later.

See/run scripts in the `templates/scripts/plantuml/` directory to generate examples from the `PlantUML` web page.   
(NOTE: Currently only examples for the sequence diagrams).

HTML documentation is located at:    
http://bitbucket.org/novakmi/plantumlbuilder/downloads

With the `plantumlbuilder` any `PlantUML` diagram can be created/programmed with use of `plant` keyword.  
With use of plugins (e.g. `PlantUmlBuilderSeqPlugin`) addtional syntax (for specific diagram type) can be added.  

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


Michal Novak (<it.novakmi@gmail.com>)

[gradle_id]: http://www.gradle.org/  "Gradle"
[groovy_id]: http://www.groovy-lang.org/ "Groovy"
[plantuml_id]: http://plantuml.sourceforge.net/  "PlantUML"
