freeStyleJob("test-job") {
    description "Here is a job!"
    label 'utility-node'
    logRotator(-1, 10)
    wrappers {
        timestamps()
        colorizeOutput 'xterm'
    }

    steps {
        systemGroovyCommand '''\
        import org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildAction
        def env = build.getEnvironment(null)
        def deploy_info = GroovyPostbuildAction.createShortText("Hi there, I'm a little label!")
        build.addAction(deploy_info)'''.stripIndent()

        shell """\
        #!/bin/bash -el

        colorize() { CODE=\$1; shift; echo -e '\\033[0;'\$CODE'm'\$@'\\033[0m'; }
        red() { echo -e \$(colorize 31 \$@); }
        green() { echo -e \$(colorize 32 \$@); }
        yellow() { echo -e \$(colorize 33 \$@); }

        green Welcome `yellow to` `red fry-ci`
        """.stripIndent()
    }
}
