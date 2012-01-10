/*
Copyright (c) 2012 Michal Novak (bubbles.way@gmail.com)
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
