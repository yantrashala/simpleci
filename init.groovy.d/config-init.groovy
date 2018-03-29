import jenkins.model.*;
import hudson.tasks.Shell;
import javaposse.jobdsl.plugin.*;
import org.jenkinsci.plugins.workflow.libs.*
import hudson.scm.SCM;
import hudson.plugins.git.*;
import jenkins.plugins.git.GitSCMSource;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import java.util.Base64;

	// Fetch values from Environment Variables
	def shared_library_url 		= System.getenv("SHARED_LIBRARY_URL")
	def shared_library_version 	= System.getenv("SHARED_LIBRARY_VERSION")
	def shared_library_name 	= System.getenv("SHARED_LIBRARY_NAME")
	def inst 					= Jenkins.getInstance()
	def desc 					= inst.getDescriptor("org.jenkinsci.plugins.workflow.libs.GlobalLibraries")
	def home_dir 				= System.getenv("JENKINS_CONF")
	def properties 				= new ConfigSlurper().parse(new File("$home_dir/simpleci.conf").toURI().toURL())
	
	def descript
	
	// Jenkins Credentials
	properties.credentials.each() { configName, serverConfig ->
	
		// Decode password using base64
		byte[] valueDecoded =  Base64.getDecoder().decode(serverConfig.password);
		Credentials creds = (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
													  serverConfig.credentialsId,
													  serverConfig.description,
													  serverConfig.username,new String(valueDecoded)
													  /*new File(serverConfig.path).text.trim()*/)
		descript = serverConfig.credentialsId;
		SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), creds)
	}

	if(shared_library_url!=null && shared_library_version!=null && shared_library_name!=null ){
		
		//This is for Modern SCM as Retrieval Method
		SCMSourceRetriever retriever = new SCMSourceRetriever(new GitSCMSource(
			"scmSourceId",
			shared_library_url,
			descript,
			"*",
			"",
			false))
		
		def name = shared_library_name  
		LibraryConfiguration libconfig = new LibraryConfiguration(name, retriever)
		libconfig.setDefaultVersion(shared_library_version)
		libconfig.setImplicit(true)
		libconfig.setAllowVersionOverride(false)
		desc.get().setLibraries([libconfig])
	}
	//In absence of env_var, look into property file for configurations
	else{
	
		properties.sharedLibrary.each() { configName, serverConfig ->
			// This is for Modern SCM as Retrieval Method
			SCMSourceRetriever retriever = new SCMSourceRetriever(new GitSCMSource(
				"scmSourceId",
				serverConfig.githubURL,
				descript,
				"*",
				"",
				false))
			
			def name = serverConfig.libraryName  
			LibraryConfiguration libconfig = new LibraryConfiguration(name, retriever)
			libconfig.setDefaultVersion(serverConfig.version)
			libconfig.setImplicit(true)
			libconfig.setAllowVersionOverride(false)
			desc.get().setLibraries([libconfig])
		}
	}
