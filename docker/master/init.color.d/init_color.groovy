// Including these here allows us to use colorized output in any scripts
// that run on the Jenkins JVM (systemGroovy, groovyPostBuild, etc)

String.metaClass.colorize = { code -> "${(char)27}[${code}m${delegate}${(char)27}[0m" }
String.metaClass.red      = { -> delegate.colorize(31) }
String.metaClass.green    = { -> delegate.colorize(32) }
String.metaClass.yellow   = { -> delegate.colorize(33) }
String.metaClass.blue     = { -> delegate.colorize(34) }
String.metaClass.magenta  = { -> delegate.colorize(35) }
String.metaClass.cyan     = { -> delegate.colorize(36) }

println "JENKINS CONFIG: ${'Setting'.red()} ${'up'.green()} ${'colorized'.cyan()} ${'output'.magenta()}".yellow()
