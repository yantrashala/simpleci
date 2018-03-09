import jenkins.model.*;
import hudson.tasks.Shell;
import javaposse.jobdsl.plugin.*;
import org.jenkinsci.plugins.workflow.libs.*
import hudson.scm.SCM;
import hudson.plugins.git.*;
import jenkins.plugins.git.GitSCMSource;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsScope;

	def inst = Jenkins.getInstance()
	def desc = inst.getDescriptor("org.jenkinsci.plugins.workflow.libs.GlobalLibraries")

  // 	Jenkins SSH Credentials
	def system_credentials_provider = SystemCredentialsProvider.getInstance()	
	def ssh_key_scope = CredentialsScope.GLOBAL
  def ssh_key_id = "jenkins-master"
  def ssh_key_username = "jenkins"
  def ssh_key_private_key_source = new BasicSSHUserPrivateKey.UsersPrivateKeySource()
  def ssh_key_passphrase = null
 	def ssh_key_description = "Jenkins Master"
  def ssh_key_domain = com.cloudbees.plugins.credentials.domains.Domain.global()
  def ssh_key_creds = new BasicSSHUserPrivateKey(ssh_key_scope,ssh_key_id,ssh_key_username,ssh_key_private_key_source,ssh_key_passphrase,ssh_key_description)

  system_credentials_provider.addCredentials(ssh_key_domain,ssh_key_creds)
	// this is for Legacy SCM Retrieval Method
	// SCM scm = new GitSCM("https://git.example.com/foo.git")
	// SCMRetriever retriever = new SCMRetriever(scm)
	
	// This is for Modern SCM as Retrieval Method
	SCMSourceRetriever retriever = new SCMSourceRetriever(new GitSCMSource(
        "someId",
        "https://github.com/CodeValet/master.git",
        "jenkins-master",
        "*",
        "",
        false))
	
	def name = "global-shared-library"    
	LibraryConfiguration libconfig = new LibraryConfiguration(name, retriever)
	libconfig.setDefaultVersion('master')
	libconfig.setImplicit(true)
	libconfig.setAllowVersionOverride(false)
	desc.get().setLibraries([libconfig])
