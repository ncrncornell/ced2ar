package edu.cornell.ncrn.ced2ar.eapi;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Clones a remote repository after the application context is loaded. 
 * Code will be executed when application is started, context is refreshed and application is shutdown.
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class WebInitializer implements ApplicationListener{
	private static final Logger logger = Logger.getLogger(WebInitializer.class);
	
	//Application shutdown gets triggered multiple times, do not want multiple pushes
	private static boolean shutdown = false;
		
	@Autowired
	private VersionControl versionControl;
	
	@Autowired
	private ServletContext context;
	
	/**
	 * When application context is loaded, this method checks for a repo,
	 * if there is not one it clones it, commits changes to the local repo 
	 * and finally pushes them to remote
	 * @param applicationEvent ApplicationEvent
	 */
	public void onApplicationEvent(ApplicationEvent applicationEvent){	
	
		logger.debug("Application Event  " + applicationEvent.getClass());
		// ContextStartedEvent is not fired always. Don't know why...
		if(applicationEvent instanceof ContextRefreshedEvent){
			logger.debug("Version control is starting");
			if(versionControl.hasLocalRepo()){
				logger.debug("Found local repository");
			}else{
				logger.debug("Cloning remote repository from: " + versionControl.getRemoteRepoURL()) ;
				versionControl.cloneRemote();
				logger.debug("Done cloning remote repository");
			}
	
			// ------- BaseX Passwords Randomizing process START -----
			// --------To enable uncomment the block of code below----
			// -------------------------------------------------------
			//logger.debug("Starting password randomizer process ...");
			//BaseXPasswordRandomizer baseXPasswordRandomizer = new BaseXPasswordRandomizer(); 
			//baseXPasswordRandomizer.randomizePasswords();
			//logger.debug("Done password randomizer process.");
			// -------------------------------------------------------
			// ------- BaseX Passwords Randomizing process END -------
			// -------------------------------------------------------
			
		}else if(applicationEvent instanceof ContextClosedEvent && !shutdown){ 
			//ContextStoppedEvent is not fired always. Don't know why...
			logger.debug("Application shutting down, commiting local changes");
			try{
				shutdown = true;
				versionControl.commit("Auto commit on application shutdown");
				logger.debug("Finshed commiting local changes");
			}catch(IOException|GitAPIException e){
				e.printStackTrace();
				logger.warn("There is an error in commiting the changes to local repo: " + e.getMessage());
			}
			
			finally{
				versionControl = null;
			}
		}
	}
}