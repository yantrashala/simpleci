import jenkins.model.*;
import jenkins.branch.OrganizationFolder;
import jenkins.scm.api.trait.SCMTrait;
import com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketEndpointConfiguration;
import com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketServerEndpoint;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator;
import com.cloudbees.jenkins.plugins.bitbucket.BranchDiscoveryTrait;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import java.util.Base64;

	// Fetch values from Environment Variables
	def bitbucker_url = System.getenv("BITBUCKET_URL")
	def bitbucket_username = System.getenv("BITBUCKET_USERNAME")
	def bitbucket_pass = System.getenv("BITBUCKET_PASS")
	def bitbucket_project = System.getenv("BITBUCKET_PROJECT")

	//Get instance of Jenkins
	jenkins = Jenkins.instance
	def parent = Jenkins.getInstance()

	// configuring the bitbucket project using environment variable
	if (bitbucker_url!=null && bitbucket_username!=null && bitbucket_pass!=null) {
		
		//Jenkins Credentials
	
		String id = java.util.UUID.randomUUID().toString()
		// Decode password using base64
		byte[] valueDecoded =  Base64.getDecoder().decode(bitbucket_pass);
		Credentials c = (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,id, bitbucket_username, bitbucket_username, new String(valueDecoded))
		SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), c)
		
		//Add bitbucket end point URLS to server list
		BitbucketEndpointConfiguration
						.get().addEndpoint(new BitbucketServerEndpoint("bitbucket", bitbucker_url, false, null));
				
		// Create the new item of Bitbucket Team/ Project Type 
		OrganizationFolder teamFolder = parent.createProject(OrganizationFolder.class, bitbucket_project);
		//Add Owner information
		BitbucketSCMNavigator navigator = new BitbucketSCMNavigator(bitbucket_project);

		
		// preselect the bitbucket server url and start scan here
		navigator.setBitbucketServerUrl(bitbucker_url);
		teamFolder.getNavigators().add(navigator);
		
		//Add only Discovery behaviour to navigator.
		List<SCMTrait<? extends SCMTrait<?>>> traitsNew = new ArrayList<>();
		traitsNew.add(new BranchDiscoveryTrait(true, true));
		navigator.setTraits(traitsNew);
		navigator.setCredentialsId(id)
		teamFolder.scheduleBuild2(0).getFuture().get();
		teamFolder.getComputation().writeWholeLogTo(System.out)
	}
	// in absence of env_var, look into property file for configurations
	else{
	
		def home_dir = System.getenv("JENKINS_CONF")
		def properties = new ConfigSlurper().parse(new File("$home_dir/simpleci.conf").toURI().toURL())
		
		properties.bitbucketConfig.each() { configName, serverConfig ->
						
			//Jenkins Credentials
	
			String id = java.util.UUID.randomUUID().toString()
			// Decode password using base64
			byte[] valueDecoded = Base64.getDecoder().decode(serverConfig.password);
			Credentials c = (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,id, serverConfig.username, serverConfig.username, new String(valueDecoded))
			SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), c)
			
			//Add bitbucket end point URLS to server list
			BitbucketEndpointConfiguration
							.get().addEndpoint(new BitbucketServerEndpoint("bitbucket", serverConfig.serverUrl, false, null));
					
			// Create the new item of Bitbucket Team/ Project Type 
			OrganizationFolder teamFolder = parent.createProject(OrganizationFolder.class, serverConfig.project);
			//Add Owner information
			BitbucketSCMNavigator navigator = new BitbucketSCMNavigator(serverConfig.project);

			// preselect the bitbucket server url and start scan here
			navigator.setBitbucketServerUrl(serverConfig.serverUrl);
			teamFolder.getNavigators().add(navigator);
			
			//Add only Discovery behaviour to navigator.
			List<SCMTrait<? extends SCMTrait<?>>> traitsNew = new ArrayList<>();
			traitsNew.add(new BranchDiscoveryTrait(true, true));
			navigator.setTraits(traitsNew);
			navigator.setCredentialsId(id)
			teamFolder.scheduleBuild2(0).getFuture().get();
			teamFolder.getComputation().writeWholeLogTo(System.out)
			
			
		}	
	}
