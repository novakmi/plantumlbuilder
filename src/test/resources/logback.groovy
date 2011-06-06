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

import ch.qos.logback.classic.boolex.JaninoEventEvaluator
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.filter.EvaluatorFilter
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.TRACE
import static ch.qos.logback.core.spi.FilterReply.*

appender("FILE_LOG", FileAppender) {
        append = false
        file = "plantumlbuilder.log"
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
        filter(LevelFilter) {
                level = ERROR
                onMatch = DENY
                onMismatch = NEUTRAL
        }
        filter(EvaluatorFilter) {
                evaluator(JaninoEventEvaluator) {
                        expression = 'logger.contains(".test.")'
                }
                onMatch = DENY
                onMismatch = ACCEPT
        }
}

appender("FILE_TEST", FileAppender) {
        append = false
        file = 'plantumlbuilder.testng'
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
        filter(EvaluatorFilter) {
                evaluator(JaninoEventEvaluator) {
                        expression = 'logger.contains(".test.")'
                }
                onMatch = ACCEPT
                onMismatch = DENY
        }
}

appender("FILE_ERR", FileAppender) {
        append = false
        file = 'plantumlbuilder.err'
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
        filter(LevelFilter) {
                level = ERROR
                onMatch = ACCEPT
                onMismatch = DENY
        }
}

appender("STDOUT", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
}

root(TRACE, ["STDOUT", "FILE_LOG", "FILE_ERR", "FILE_TEST"])