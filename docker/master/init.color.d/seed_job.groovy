import jenkins.model.*;
import hudson.model.FreeStyleProject;
import hudson.plugins.git.GitSCM;
import hudson.tasks.Shell;
import javaposse.jobdsl.plugin.*;

import hudson.plugins.groovy.StringSystemScriptSource
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript
import hudson.plugins.groovy.SystemGroovy
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration

// def addGroovyStep(job, scriptContent) {
//     def stringSource = new StringSystemScriptSource(new SecureGroovyScript(scriptContent, false))
//     def groovyStep = new SystemGroovy(stringSource)
//     job.buildersList.add(groovyStep)
//
//     // Approve script
//     def scriptApproval = ScriptApproval.get()
//     scriptApproval.preapprove(scriptContent, new GroovyLanguage())
//     scriptApproval.save()
// }

def url = "https://github.com/brandon-fryslie/fry-jenkins.git"
def jobName = "seed-job"

if (Jenkins.instance.itemMap.'seed-job') {
  Jenkins.instance.getItem('seed-job').delete()
}

def seedJob = Jenkins.instance.createProject(FreeStyleProject, jobName)
def gitScm = new GitSCM(url)
gitScm.branches = [new hudson.plugins.git.BranchSpec("*/master")]
seedJob.scm = gitScm

seedJob.getBuildersList().clear()

String seedJobsDir = 'jobs/dsl'

println "THIS IS CRAZY".cyan()

// Add step to process job DSL
def executeDslScripts = new ExecuteDslScripts()
executeDslScripts.setTargets("${seedJobsDir}/**/*.groovy")
executeDslScripts.setRemovedJobAction(RemovedJobAction.DELETE)
executeDslScripts.setRemovedViewAction(RemovedViewAction.DELETE)
executeDslScripts.setLookupStrategy(LookupStrategy.JENKINS_ROOT)

seedJob.buildersList.add(executeDslScripts)


// Add step to preapprove all pending scripts
// addGroovyStep(seedJob, '''\
//     import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
//     println "JENKINS CONFIG: Pre-approving all groovy scripts as part of seed job".yellow()
//     def scriptApproval = ScriptApproval.get()
//     scriptApproval.preapproveAll()
//     scriptApproval.save()
// '''.stripIndent())

// Add step to remove all triggers in non-primary CI systems
// if (System.env.JENKINS_TYPE != 'primary') {
//     println "JENKINS CONFIG: Adding step to remove triggers in non-primary CI system".yellow()
//
//     addGroovyStep(seedJob, '''\
//         import jenkins.model.Jenkins
//         import hudson.model.*
//         import hudson.triggers.*
//         if (build.buildVariableResolver.resolve("REMOVE_TRIGGERS")?.toBoolean()) {
//             println "JENKINS CONFIG: Removing all triggers from jobs in test system".yellow()
//             Jenkins.instance.getAllItems(AbstractProject.class).each { item ->
//                 if (item.name == 'seed-job') {
//                     println "JENKINS CONFIG: Remove triggers - Ignoring seed-job".yellow()
//                     return
//                 }
//                 def triggers = item.getTriggers()
//                 if (triggers.size() == 0) return;
//                 triggers.each { descriptor, trigger ->
//                     println "JENKINS CONFIG: Removing trigger '${trigger}' on job '${item.name}'".yellow()
//                     item.removeTrigger(descriptor);
//                 }
//             }
//         } else {
//             println "JENKINS CONFIG: Skipping trigger removal in test system. Be careful!".yellow()
//         }
//         true // return true from script, value is printed but not used for anything
//     '''.stripIndent())
// }

// Disable approval for Job DSL scripts
def globalSecurityConfig = Jenkins.instance.getDescriptor(GlobalJobDslSecurityConfiguration.class)
globalSecurityConfig.useScriptSecurity = false
globalSecurityConfig.save()

seedJob.save()
