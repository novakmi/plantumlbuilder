//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/plantumlbuilder/src/LICENSE)

class UmlSeq {

        final static def name = "sequence" // groovy makes automatically getName(), setName()
        final static def user = 'user'
        final static def system = 'system'

        static def buildUml(builder) {

                builder.plantuml("${getName()}.png") {
                        title('Plantuml sequence diagram template example')
                        actor(user)
                        participant(system)
                        msgAd(user, to: system, text: 'start', returnText: 'result') {
                                msgAd(system, to: user, text: 'request data')
                                msg(system, text: 'calculate result')
                        }
                        msg(user, to: system, text: 'turn off', close: 'destroy')
                }
        }

}
