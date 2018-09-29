package edu.cornell.ncrn.ced2ar.eapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand.Stage;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import edu.cornell.ncrn.ced2ar.api.data.BaseX;
import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.api.data.ConfigurationProperties;
import edu.cornell.ncrn.ced2ar.api.data.Fetch;
import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookData;
import edu.cornell.ncrn.ced2ar.eapi.rest.queries.EditCodebookData;

/**
 *Manages the version control workflow via Git
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty, Kyle Brumsted, Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class VersionControl {
	
	private static final Logger logger = Logger.getLogger(VersionControl.class);
	public static final String gitWorkingDirectory = "git";
	public static final String gitRemoteCopyDirectory = "gitRemoteCopy";
	
	private boolean loadedBaseX = false;
	private final String branchPrefix = "refs/heads/";	
	private final String startPoint = "origin/master";
	private final String webAppName = "ced2ar-web";
	private final String gitMasterDirectory = "gitMaster";

	private String remoteRepoURL;
	private String remoteBranch;
	private String remoteUser;
	private String remotePass;
	private String localBranch;
	private boolean isGitEnabled;
	private int numberOfCommitsToPushRemote;

	//TODO: Might want to remove context references
	@Autowired
	private ServletContext context;

//Scheduled tasks
	
	/**
	 *Commits all the staged files
	 */	
	@Async
	@Scheduled(cron="${gitLocalCommitCronExpression}")
	public void taskCommitRepo(){
		
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
			return;
		}	
		
		//TODO: Maybe run less frequently
		long startTime = System.currentTimeMillis();
		try{
			logger.debug("Start Record all Commits");
			recordAllCommits();
		}catch(IOException | GitAPIException e1){
			logger.error(e1.getLocalizedMessage());
			e1.printStackTrace();
		}
		finally {
			logger.debug("Record All Commits ended. Time " + (System.currentTimeMillis() - startTime) + "Milli Seconds" );
		}
		
		startTime = System.currentTimeMillis();
		try{
			logger.debug("Commit repo task called");
				
			
			//Loads current codebooks from BaseX once
			//TODO: might want to make optional
			if(BaseX.testConnection() && !loadedBaseX){
				logger.debug("Checking BaseX for unstaged codebooks...");
				Config config = Config.getInstance();
				String port = Integer.toString(config.getPort());
				String webAppName = context.getContextPath();
				String baseURI= "http://localhost:"+port+webAppName+"/rest/";
				fillRepoFromBaseX(baseURI);		
				addCodebooksInLocalMaster();
				loadedBaseX = true;
				logger.debug("Done checking BaseX.");
			}else{
				commitPendingChanges();
			}
		}catch(IOException|GitAPIException e){
			logger.error("Error commiting: "+e.getMessage());
			e.printStackTrace();
		}
		finally {
			logger.debug("Commit repo task ended. Time " + (System.currentTimeMillis() - startTime) + "Milli Seconds" );
		}
	}
	
	/**
	 *Pushes to remote repo, generally once a day if any unpushed commits have not been taken care of
	 *This happening when number of commits ahead is greater than zero but below the threshold
	 */
	@Async
	@Scheduled(cron="${gitRemotePushCronExpression}")
	public void taskPushRepo(){	
		logger.debug("Push repo task called");
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
			return;
		}
		try{
			pushToRemote();
		}catch(GitAPIException|IOException e){
			logger.error("Error pushing to remote repo "+ e.getMessage());
			e.printStackTrace();
		}
	}
	
//Git methods
	/**
	 * Checks if any changes are staged but not committed
	 * @return whether or not uncommitted changes are made
	 */
	private boolean hasChanges(){
		Git git = null;
		try{
			git = openRepo();
		    StatusCommand sc = git.status();
		    Status status = sc.call();
		    Set<String> changedSet = status.getChanged();
		    Set<String> addedSet = status.getAdded();
		    if(changedSet.size() > 0 || addedSet.size() > 0){
		    	logger.debug("Changes found");
		    	return true;
		    }
		}catch(IOException | GitAPIException e) {
			logger.error("Error " + e.getLocalizedMessage());
			e.printStackTrace();
		}finally{
			if(git != null) git.close();
		}    
		logger.debug("No changes found");
		return false;
	}
	
	/**
	 * Returns a String that represents latest committed version of codebook from local working repository.
	 * If there is no codebook in the repository, an empty string is returned    
	 * @param codebookName
	 * @return 
	 * @throws IOException
	 */
	private String getCodebook(String codebookName) throws IOException{
		return getCodebook(codebookName,false);
	}

	/**
	 * Returns a String that represents latest committed version of codebook.
	 * If there is no codebook in the repository, an empty string is returned    
	 * @param  codebookName  Name of the Codebook
	 * @param  useRemoteCopy  switch to use local working or local remote copy as repository
	 * @return contents of the Codebook as a String
	 * @throws IOException 
	 */	
	
	private String getCodebook(String codebookName,boolean useRemoteCopy) throws IOException{
		//TODO:Method will be made public once the versioning is exposed to the user
		Git git = null;
		Repository repo = null;
		RevWalk revWalk = null;
		String codebookAsString  = "";
		try{
			if(useRemoteCopy)
				git = openRepo(this.getGitDirectory(gitRemoteCopyDirectory));
			else 
				git = openRepo();
		    repo = git.getRepository();
		    ObjectId lastCommitId = repo.resolve(Constants.HEAD);
		    revWalk = new RevWalk(repo);
		    RevCommit commit = revWalk.parseCommit(lastCommitId);
		    codebookAsString = getCodebook(codebookName, repo, commit);
		}finally{
			if(repo != null) repo.close();
			if(git != null) git.close();
			if(revWalk != null) revWalk.close();
		}
	    return codebookAsString;	    
	}
	
	/**
	 * Retrieves the names of all codebooks currently in the repository
	 * @return an ArrayList of all of the names
	 * @throws IOException 
	 */
	private ArrayList<String> getCodebooks() throws IOException{
		ArrayList<String> names = new ArrayList<String>();
		Git git = null;
		Repository repo = null;
		RevWalk revWalk = null;
		TreeWalk treeWalk = null;
		try{
			git = openRepo();
		    repo = git.getRepository();
		    ObjectId lastCommitId = repo.resolve(Constants.HEAD);
		    revWalk = new RevWalk(repo);
		  //TODO: Following line throws null pointer exception if the remote branch does not exist
		    RevCommit commit = revWalk.parseCommit(lastCommitId); 
		    RevTree tree = commit.getTree();

		    treeWalk = new TreeWalk(repo);
		    treeWalk.addTree(tree);
		    treeWalk.setRecursive(true);
		    while(treeWalk.next()){
		    	logger.debug("found codebook in repo: "+treeWalk.getPathString());
		    	names.add(treeWalk.getPathString());
		    }
		}catch(NullPointerException NPE) {
	    	logger.debug("Error. Does remote Branch Exist?");
			throw NPE;
		}
		finally{
			if(repo != null) repo.close();
			if(git != null) git.close();
			if(revWalk != null) revWalk.close();
			//if(treeWalk != null) treeWalk.close();
			if(treeWalk != null) treeWalk.close();
		}
	    return names;
	}
 
	/**
	 * Stages the file in the local repository.  
	 * @param fileName the name of the file to stage
	 * @param codeBook Content the content to be written to the file
	 * @param message the commit message
	 * @param pathToAdd the directory holding the repository
	 * @throws IOException 
	 * @throws GitAPIException 
	 */
	public void stageCodebook(String fileName,String codeBookContent, String pathToAdd) 
	throws IOException, GitAPIException {
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting");
			return;
		}
		if(!fileName.contains("."))
			 fileName = toRepoFormat(fileName);
		Git git = null;
		File file;
		try{
			String path = getGitDirectory(gitWorkingDirectory);
			file = new File(path, fileName);
			writeRepository(file, codeBookContent);
			git = openRepo();
			AddCommand add = git.add();
			add.addFilepattern(pathToAdd).call();
		}finally{
			if(git != null) git.close();
		}
	}
	
	/**
	 * To be called from BaseX, when editing ui updates a codebook
	 * Necessary because BaseX stores codebooks without namespaces
	 * but git has namespaces, as does the /rest/codebooks/{handle} API endpoint
	 * @param baseURI
	 * @param handle
	 */
	public void stageCodebookB(String handle, String pathToAdd) 
	throws IOException, GitAPIException {
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting");
			return;
		}
		
		String fileName = toRepoFormat(handle);
		String content = BaseX.get(handle);
		XMLHandle xh = new XMLHandle(content, Config.getInstance().getSchemaURI());
		xh.addNamespace();
		content = xh.docToString();
		Git git = null;
		try{
			String path = getGitDirectory(gitWorkingDirectory);
			File file = new File(path, fileName);
			writeRepository(file, content);
			git = openRepo();
			AddCommand add = git.add();
			add.addFilepattern(pathToAdd).call();
		}finally{
			if(git != null) git.close();
		}
	}		
	
	/**
	 * Commits all the staged file in the local git repository
	 * @param message String commit message
	 * @param force boolean decides whether or not to get if any changes exist
	 * @throws IOException
	 * @throws GitAPIException
	 */
	protected void commit(String message) throws IOException, GitAPIException{
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
			return;
		}	
		if(hasChanges()){
			logger.debug("Commit message"+ message);
			Git git = null;
			try{
				git = openRepo();
				CommitCommand commit = git.commit();
				commit.setMessage(message).call();
				logger.debug("Added local commit: "+message);
				if(getNumBehindLocal() >= getNumberOfCommitsToPushRemote()){ 
					pushToRemote();
				}
			}finally{
				if(git !=null) git.close();
			}	
		}
	 }
	
	/**
	 * Returns the number of commits not yet pushed to the remote repo
	 * If this number is greater than or equal to the threshold, it will record the commits in BaseX
	 * @return int number of commits since last push
	 */
	//TODO:This needs to have some constants removed
	private int getNumBehindLocal(){	
		logger.debug("Listing remote repository " + getRemoteRepoURL());
        Collection<Ref> refs;
        String ID = "";
        int count = 0;
        Repository repo = null;
        Git git = null;
        RevWalk walk = null;
		try{
			refs = Git.lsRemoteRepository()
		        .setHeads(true)
		        .setTags(true)
		        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(getRemoteUser(),getRemotePass()))
		        .setRemote(getRemoteRepoURL())
		        .call();
			 for(Ref ref : refs){
				if(ref.toString().contains(getRemoteBranch())){
					String[] spl = ref.toString().split("=");
					ID = spl[1].replace("]","");
					logger.debug("Hash for branch '"+getRemoteBranch()+"' is: "+ID);
				}
		     }
			 git = openRepo();//open local repository
			 repo = git.getRepository();
			 Ref head = repo.getRef(this.branchPrefix+this.getLocalBranch());
			 walk = new RevWalk(repo);
			 RevCommit commit = null;
			 try{
				 commit = walk.parseCommit(head.getObjectId());
			 }catch(NullPointerException e){
				 //Commit log is blank, push to initialize
				 return getNumberOfCommitsToPushRemote() + 1;
			 }
			 logger.debug("Start commit: "+commit);
			 logger.debug("Walking all commits starting at head");
			 walk.markStart(commit);
			 List<RevCommit> pendingCommits = new ArrayList<RevCommit>();
			 for(RevCommit rev : walk){
				 String[] parts = rev.toString().split(" ");
				 logger.debug("Checking "+parts[1]);
				 if(ID.equals(parts[1])){
					 logger.debug("Found matching commit. Remote repo is "+count+" commits off local.");
					 break;
				 }else{
					 pendingCommits.add(rev);
				 }
				 count++;//Should test before incrementing
			 }	 
		}catch(IOException|GitAPIException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(repo != null) repo.close();
			if(git != null) git.close();
			if(walk != null) walk.close();
		}
		return count;
	}

	/**
	 * Gets the list of commits from the log
	 * can be turned public once versioning is exposed to the user
	 * @return List of RevCommit objects.
	 * @throws IOException 
	 * @throws GitAPIException 
	 */
	protected List<RevCommit> getCommitLog(String path) throws  IOException, GitAPIException {
		 Iterable<RevCommit> log;
		 Git git  = null;
		 ArrayList<RevCommit> commitLog = new ArrayList<RevCommit>();
		 try{	 
			 git = openRepo();
			
			 log = git.log().addPath(path).call();
			 Iterator<RevCommit> i = log.iterator();
			 while(i.hasNext()){
				 RevCommit rc = (RevCommit)i.next();
				 commitLog.add(rc);
			 }
		 }catch(NullPointerException e){
			 logger.debug("Commit log is empty");
			 e.printStackTrace();
		 }
		 finally{
			 if(git !=null) git.close(); 
		 }
		 return commitLog;
	 }
	
	/**
	 * Writes all commit info to BaseX DB
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public void recordAllCommits() throws IOException, GitAPIException {
		Repository repo = null;
		Git git = null;
		RevWalk walk = null;
		try{
		    git = openRepo();
		    repo = git.getRepository();
		    walk = new RevWalk(repo); 
		    //TODO: Sometimes throwing NoHeadException
	        Iterable<RevCommit> commits = git.log().all().call();
	        for(RevCommit commit : commits){	
	        	String local = "false";
				String fullMessage = commit.getFullMessage();
				String hash = commit.getId().getName();
				String timestamp = Integer.toString(commit.getCommitTime());
				
				RevCommit targetCommit = walk.parseCommit(repo.resolve(commit.getName()));
	            for (Entry<String, Ref> e : repo.getAllRefs().entrySet()){
	                if (e.getKey().startsWith(Constants.R_HEADS)) {
	                    if(walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
	                        String branchName = e.getValue().getName();
	                        branchName = branchName.replace("refs/heads/", "");
	                        if(getLocalBranch().equals(branchName)) {
	                        	local = "true";
	                            break;
	                        }
	                    }
	                }
	            }
	            /**
	             * Getting a SimpleAsyncUncaughtExceptionHandler...ArrayIndexOutOfBoundsException Error. 
	             * This prevented some of the git tasks from being done.
	             * The error was occurring during the scheduled run of VersionControl.taskCommitRepo().
	             * The underlying cause of the error was that a commit message fragment/part had a type=var and no variable name.
	             * 
	             * Rules:
	             *   1) For this to work properly, a type=var MUST have a variable name.
	             *   2) A var name is required, but having an empty string is still considered valid xml.
	             * 
	             * Behavior:
	             *   Not updating the variable in git and logging a message.  
	             */
				Pattern pattern = Pattern.compile("\\{(.+?)?\\}");
				Matcher matcher = pattern.matcher(fullMessage);
				Map<String,List<String>> commitVars = new HashMap<String,List<String>>();
				List<String> handles = new ArrayList<String>();
				String user = "anonymous";
				while (matcher.find()) {
				   String[] data = matcher.group(1).split(",");
				   	try{
				   		/**
				   		 * Prevent the ArrayIndexOutOfBoundsException Error.
				   		 * Check the length of the data array to see if we have a var name in the array.
				   		 * If length > 3 
				   		 *   then do the normal processing.
				   		 *   else Skip the normal processing and log a message with references to offending commit message part/fragment.
				   		 */
					   if(data[2].equals("var")){
						 if(data.length > 3){
						   String handle = data[0];
						   if(!handles.contains(handle))
							   handles.add(handle);
						   String name = data[3];                // was line 531 before v2.8.2.0
						   if(commitVars.containsKey(handle)){
							   List<String> vars = commitVars.get(handle);
							   if(!vars.contains(name)){
								   vars.add(name); 
								   commitVars.put(handle, vars);
							   }   
						   }else{
							   List<String> vars = new ArrayList<String>();
							   vars.add(name);
							   commitVars.put(handle, vars);
							   logger.debug(handle + " "+name);
						   } 
						  }else{
							  // var type with no variable name
							  String warningMesssage = "git commit message part not processed. var type does not have a variable name.  ";
							  logger.warn(warningMesssage + "commit Id: " + hash + "   matcher.group (handle, user, type): " + matcher.group(1));
							   }
						 }
					   // end if(data[2]
					   user = data[1];
				   	}catch(NullPointerException e){
				   		logger.error("Null pointer for git log"+matcher.group(1));
				   	}
				}		
				QueryUtil.insertCommit(hash, local, timestamp, user, handles);
				QueryUtil.insertVarCommit(hash, commitVars);
			}
		}finally{
			if(repo != null) repo.close();
			if(git != null) git.close();
			if(walk != null) walk.close();			
		}
	}

	/**
	 * boolean test to see if there exists a GIT repo in the working directory provided
	 * @return a boolean whether or not the directory has a git repo
	 */
	public boolean hasLocalRepo(){
		String path = getGitDirectory(gitWorkingDirectory)+"/.git";
		if(RepositoryCache.FileKey.isGitRepository(new File(path), FS.DETECTED))
			return true;
		else
			return false;
	}
	
	/**
	 * Clones the repository specified by the remoteURL variable into 
	 * the directory specified by the gitWorkingDirectoruy variable
	 */
	public void cloneRemote(){
		if(!isGitEnabled()){
			logger.debug("Version Control is not enabled. Exiting.");
			return;
		}
		File localPath = null;
		File remoteCopyPath = null;
		File masterPath = null;
        //localPath.delete();
        Repository repository = null;       
        try{
        	
        	localPath = new File(getGitDirectory(gitWorkingDirectory));
    		remoteCopyPath = new File(getGitDirectory(gitRemoteCopyDirectory));
    		masterPath = new File(getGitDirectory(gitMasterDirectory));
    		
    		//Adds master copy TODO: logic if running the master copy, and make configurable
	        Git.cloneRepository()
            .setURI(remoteRepoURL)
            .setDirectory(masterPath)
            .setBranch(branchPrefix+"master")
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
            .call();
	        
        	if(remoteContainsBranch(remoteBranch)) {
        		logger.debug("Branch exists in the remote reposiory: " + remoteBranch);
	        	logger.debug("Cloning into " + localPath);
		        Git.cloneRepository()
		            .setURI(remoteRepoURL)
		            .setDirectory(localPath)
		            .setBranch(branchPrefix+remoteBranch)
		            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
		            .call();
		       
        		//Adds remote read only copy
		        Git.cloneRepository()
		            .setURI(remoteRepoURL)
		            .setDirectory(remoteCopyPath)
		            .setBranch(branchPrefix+remoteBranch)
		            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
		            .call();
        		logger.debug("Successfully cloned remote repository into localWorkingDirectory and localRemoteCopy");
        	}else{
        		logger.debug("Branch does NOT exists in the remote reposiory: " + remoteBranch);
		        Git.cloneRepository()
	            	.setURI(remoteRepoURL)
	            	.setDirectory(localPath)
	            	.setBranch(branchPrefix+remoteBranch)
	            	.setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
	            	.call();
		        //Adds remote read only copy
		        Git.cloneRepository()
	            	.setURI(remoteRepoURL)
	            	.setDirectory(remoteCopyPath)
	            	.setBranch(branchPrefix+remoteBranch)
	            	.setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
	            	.call();
        	}

        	logger.debug("Opening repository...");
	        FileRepositoryBuilder builder = new FileRepositoryBuilder();
	        repository = builder.setGitDir(localPath)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	if(repository != null) repository.close();
        }
	}
	
	/**
	 * Pushes commits to the remote repo
	 * @throws GitAPIException 
	 * @throws IOException 
	 */
	public void pushToRemote() throws GitAPIException, IOException{
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
		}
		
		long startTime = System.currentTimeMillis();
		logger.debug("Version control push to remote repository started");
		Git git = null;			
		try{
			RefSpec ref = new RefSpec(branchPrefix+remoteBranch+":"+branchPrefix+remoteBranch);
			git = openRepo();
			Iterable<PushResult> rs = git.push()
				.setRemote(remoteRepoURL)
				.setRefSpecs(ref)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.getRemoteUser(),this.getRemotePass()))
				.call();
			for(PushResult pushResult : rs) {
				logger.debug(pushResult.getMessages());
				logger.debug(pushResult.toString());
			}
			long endTime = System.currentTimeMillis();
			logger.debug("Version control push to remote repository ended, taking "+(endTime-startTime)+"ms");
		}finally{
			if(git != null) git.close();
		}	
	}

	/**
	 * Takes all codebooks that are in the CED2AR database in BaseX and not in repo, and stages them
	 * @param baseURI base uri of application
	 */
	private void fillRepoFromBaseX(String baseURI){
		logger.debug("Start ");
		long start = System.currentTimeMillis();
		boolean newCodebooks = false;
		try {
			List<String> current = getCodebooks(); // get codebooks in git
			long start1 = System.currentTimeMillis();
			Collection<String[]> codebooks = Fetch.getCodebooks(baseURI).values();  // get codebooks in basex
			long end1 = System.currentTimeMillis();
			logger.debug("fetchtime Fetch." + (end1-start1));
			for(String[] codebook : codebooks){
				String handle = codebook[0]+codebook[1];
				String fileName = codebook[0]+"."+codebook[1]+".xml";
				if(!current.contains(fileName)){
					logger.debug("Found new codebook: "+fileName);
					String codebookURL = baseURI+"/codebooks/"+handle+"?type=git";
					String contents = Fetch.get(codebookURL);//getXML decodes ampersands
					newCodebooks = true;	
					stageCodebook(fileName, contents,".");
				}
			}
			if(newCodebooks){
				commit("Commiting codebooks retrieved directly from BaseX");
			}
			long end = System.currentTimeMillis();
			logger.debug("Time to complete fillRepoFromBaseX " + (end-start));
			
		} catch (GitAPIException|IOException e) {
			logger.error("Error pulling from BaseX on startup " + e.getMessage());
		}
	}

	/**
	* Opens an instance of Git
	* @return the Git repository object
	* @throws IOException
	*/
	private Git openRepo() throws IOException{
		return openRepo(this.getGitDirectory(gitWorkingDirectory));
	}

	/**
	 * @param remoteCopyRepo switch to determine remoteCopyRepo or localWorkingRepo
	 * @return the Git repository object
	 * @throws IOException
	 */
	private Git openRepo(String directory) throws IOException{
		return Git.open(new File(directory));
	}

	/**
	 * This method performs physical write operation of a file
	 * that would be staged in the git.
	 * @param file the name of the file to write or overwrite
	 * @param content the contents of the file to write or overwrite
	 * @see  stageCodebook
	 */
	private void writeRepository(File file, String content){
		FileWriter writer = null;
		try{
			writer = new FileWriter(file);
			writer.write(content);
		}catch(IOException e) {
			 e.printStackTrace();
		}finally{
			try{
				writer.close();
			}catch(IOException e) {
				writer = null;
				e.printStackTrace();
			}
		} 
	}
	
	/**
	 * Retrieves the a copy of a codebook from the repository and returns it as a String.
	 * If the codebook is not found, it returns an empty String
	 * @param codebookName the codebook to be retrieved
	 * @param repo the repository that holds the codebook
	 * @param commit the commit from which the codebook will be pulled
	 * @return the codebook as a string
	 * @throws IOException 
	 * @throws CorruptObjectException 
	 * @throws IncorrectObjectTypeException 
	 * @throws MissingObjectException 
	 */
	private String getCodebook(String codebookName, Repository repo, RevCommit commit) 
	throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException{
		TreeWalk treeWalk = null;
		String codebook="";
		try{
			ByteArrayOutputStream out  = null;
			RevTree tree = commit.getTree();
			treeWalk = new TreeWalk(repo);
		    treeWalk.addTree(tree);
		    treeWalk.setRecursive(true);
		    treeWalk.setFilter(PathFilter.create(codebookName));
		    if(treeWalk.next()){
			    ObjectId objectId = treeWalk.getObjectId(0);
			    ObjectLoader loader = repo.open(objectId);
			    try{
				    out = new ByteArrayOutputStream();
				    loader.copyTo(out);
				    codebook = out.toString();
			    }finally{
			    	if(out != null)out.close();
			    }
		    }
		}finally{
			treeWalk.close();
		}
	    return codebook;
	}

	/**
	 * Translates BaseX file names,ie ssb6, into repo format, ie ssb.6.xml
	 * Method call return an array of Strings.
	 * First element contains list of codebook names delimeted by a new line chars.
	 * Codebook name consists of name.version ie ssb.6
	 * 
	 * @param s name of the codebook  in the format ssb6 for ssb.6.xml
	 * @return return the repo format ex. ssb.6.xml
	 */
	private String toRepoFormat(String s){
		String temp = s.replaceAll(".xml", "");
		CodebookData codebookData =  new CodebookData();
		String codebookString  = codebookData.getCodebooks("");
		String[] codebookHandles = QueryUtil.getFullHandles();
		if(codebookHandles == null || codebookHandles.length==0 ) {
			throw new RuntimeException("BaseX does not contain any codebooks");
		}
		
		String[] codebooks  = codebookHandles [0].split("\\r?\\n");
		for(int i = 0; i < codebooks.length; i++){
			String codebook = codebooks[i].trim();  // remove newline or whitespace 
			if(codebook.replace(".", "").equals(temp))
				return codebook+".xml";

		}
		return s;
	}
	
	

	/**
	 * A Factory Method that initializes  and instantiates VersionControl Bean.
	 * This method should be used to get instance of the VersionControl before the ApplicationContext is initialized.
	 * After the ApplicationContext is initialized, @Autowired annotation or dependency injection may be used.
	 * @return instance of VersionControl
	 */		
	public static VersionControl getInstance(){		
		ClassPathXmlApplicationContext appContext = null;
		try{
			appContext = new ClassPathXmlApplicationContext("ced2ar-web-beans.xml");
			BeanFactory beanFactory=appContext;
			return (VersionControl) beanFactory.getBean("versionControl");
		}finally{
			appContext.close();
		}
	}
	
	/**
	 *Retrieves path to a git directory
	 *@return absolute path of the git repository
	 */
	//TODO:When context is not null, git working directory is extracted from the context
	//When context is null, the git directory is extracted from the url
	public String getGitDirectory(String directory) {
		String realGitDirectory ="";
		if(context == null){ 
			URL url = VersionControl.class.getResource("VersionControl.class");
			String className = url.getFile();
			String webInfPath = className.substring(0,className.indexOf("WEB-INF") + "WEB-INF".length());
			realGitDirectory=webInfPath+"/"+directory+"/"; 
		}
		else{
			realGitDirectory = context.getRealPath("/WEB-INF/"+directory+"/");
		}
		File dir = new File(realGitDirectory);
		if(!dir.exists()){
			dir.mkdir();
		}
		logger.debug("Opening directory " + realGitDirectory);
		//realGitDirectory = "c:/java/info/git/gittest/eclipse/"+directory+"/"; //local testing only
		return realGitDirectory;		
	}

	/**
	 * This method checks the remote repository for the existence of the branch.
	 * @param branchName name of the branch in the remote repository
	 * @return true if the branch found in the remote repository; false otherwise.
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 * @throws GitAPIException
	 */
    public boolean remoteContainsBranch(String branchName) throws TransportException,InvalidRemoteException,GitAPIException{
    	boolean contains = false;
        Collection<Ref> refs = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(false)
                .setRemote(remoteRepoURL)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
                .call();
        for (Ref ref : refs) {
        	if(ref.getName().equalsIgnoreCase(branchPrefix+branchName)) {
        		contains= true;
        	}
        }
        return contains;
    }

    /**
     * This method creates a new branch in the local repository, makes it the head, then pushes newly created branch to the remote.
     * This method should only be called after verifying that the branch does not exists in the remote repository
     * @param branchName
     * @throws IOException
     * @throws RefAlreadyExistsException
     * @throws RefNotFoundException
     * @throws InvalidRefNameException
     * @throws CheckoutConflictException
     * @throws GitAPIException
     */
    public void createNewBranch(String branchName) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		Git git = null;			
		try{
			git = openRepo();
	    	git.checkout().
	    	    setCreateBranch(true).
	    	    setName(branchName).
	    	    setStartPoint(startPoint).
	    	    call();
	    	logger.debug("Created a new branch called " + branchName);
	    	logger.debug("Now Pushing the newly created branch " + branchName);
	    	git.push()
				.setRemote(this.remoteRepoURL)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getRemoteUser(),getRemotePass()))
				.call();
	    	logger.debug("Successfully created and pushed new branch " + branchName);
		}
		finally {
			if(git != null) git.close();
		}
    }

    /**
     * This method detects any changes made in BaseX, 
     * commits them to local repo if there are any
     * @throws IOException
     * @throws GitAPIException
     */
    public void commitPendingChanges() throws IOException,GitAPIException {
		logger.debug("Version control commit to local repository started");
		long startTime = System.currentTimeMillis();
		String message = QueryUtil.getPending();
		logger.debug(message);
		
		//Removes duplicates
		Pattern pattern = Pattern.compile("\\{(.+?)?\\}");
		Matcher matcher = pattern.matcher(message);
		List<String> statements = new ArrayList<String>();
		List<String> handles = new ArrayList<String>();
		while(matcher.find()){
		   String statement = "{"+matcher.group(1)+"}";
		   if(!statements.contains(statement)){
			   statements.add(statement);
			   String[] info = statement.trim().replace("{", "").replace("}", "").split(",");
			   String handle = info[0];
			   if(!handles.contains(handle)){
				   logger.debug("Stagging "+handle);
				   stageCodebookB(handle, ".");
			   }
		   }
		}		
		message = StringUtils.join(statements.toArray());
		commit(message);
		logger.debug("Version control commit to local repository ended, taking " 
		+(System.currentTimeMillis()-startTime)+ "ms");
    }
    
    /**
     * This method fetches codebooks from baseX, LocalGit and RemoteGit.
     * Populates the existence status of the codebook in those places.
     * This method is not Deprecated. Uses Lists to process. 
     * A more efficient method getCodebookStatusInfoE() is implemented, which uses Maps.
     */
    public List<GitCodebook> getCodebookStatusInfoE() throws CloneNotSupportedException,GitAPIException,IOException{
		logger.debug("Start get all code books info MAP");
		long start = System.currentTimeMillis();
		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String baseURI= "http://localhost:"+port+"/"+webAppName+"/rest/";
		fillRepoFromBaseX(baseURI);
		Map<String,GitCodebook> codebookMap = new HashMap<String,GitCodebook>();
    	List<GitCodebook> codebooks = new ArrayList<GitCodebook>();
    	
    	getCodebooksInBaseX(codebookMap);
    	getCodebooksInLocalRepository(codebookMap);
    	getCodebooksInRemoteRepository(codebookMap);
    
    	if(codebookMap.isEmpty()) return codebooks;

    	Iterator IT= codebookMap.entrySet().iterator();
    	while(IT.hasNext()) {
    		Map.Entry<String,GitCodebook> pair = (Map.Entry<String,GitCodebook>)IT.next();
    		codebooks.add(codebookMap.get(pair.getKey()));
    	}
    	setCodebookSynchInfo(codebooks);
		long end= System.currentTimeMillis();

    	// This method sets the remote codebook validity status. 
    	//ie validate the codebook for ingestability into BaseX
    	//We are not doing ot here because it is taking lot of time
    	// We are going to do this when the user explicitly tries to ingest the codebook
	   // setRemoteCodebookValidityStatus(codebooks);

		logger.debug("End get all codebooks Time in MillisSeconds: " + ((end -start)));
    	return codebooks;
    }

    /**
     * This method fetches codebooks from baseX, LocalGit and RemoteGit.
     * Populates the existence status of the codebook in those places.
     * This method is not Deprecated. Uses Lists to process. 
     * A more efficient method getCodebookStatusInfoE() is implemented, which uses Maps.
     * 
     * @deprecated use {@link #getCodebookStatusInfoE()} instead. 
     */
    @Deprecated
    public List<GitCodebook> getCodebookStatusInfo() throws CloneNotSupportedException,GitAPIException,IOException{
    	logger.debug("Start get all code books info");
		long start = System.currentTimeMillis();

		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String baseURI= "http://localhost:"+port+webAppName+"/rest/";
		fillRepoFromBaseX(baseURI);		
    	
    	
    	List<GitCodebook> codebooks = new ArrayList<GitCodebook>();
    	List<GitCodebook> codebooksInLocal = this.getCodebooksInLocalRepository();
    	List<GitCodebook> codebooksInRemote = this.getCodebooksInRemoteRepository();
    	List<GitCodebook> codebooksInBaseX = this.getCodebooksInBaseX();
    	
    	if(codebooksInLocal.isEmpty()&&codebooksInRemote.isEmpty() && codebooksInBaseX.isEmpty()) {
    		return codebooks;
    	}
    	// adds codebooks in baseX to codebook array
    	for(GitCodebook codebook :codebooksInBaseX) {
    		codebooks.add((GitCodebook)codebook.clone());
    	}
    	
    	// updates codebook array setLocalGitExistanceStatus if codebook is in Local.
    	// if codebook is not in local, it just adds to codebook array
    	for(GitCodebook localCodebook :codebooksInLocal) {
    		boolean exists = false;
    		for(GitCodebook codebook :codebooks) {
    			if(codebook.getCodebookName().equals(localCodebook.getCodebookName())) {
    				exists = true;
    				codebook.setLocalGitExistanceStatus(GitCodebook.STATUS_EXISTS);
    			}
    		}
    		if(!exists) {
    			codebooks.add((GitCodebook)localCodebook.clone());
    		}
    	}

    	// updates codebook array setRemoteGitExistanceStatus if codebook is in Remote.
    	// if codebook is not in Remote, it just adds to codebook array
    	for(GitCodebook remoteCodebook :codebooksInRemote) {
    		boolean exists = false;
    		for(GitCodebook codebook :codebooks) {
    			if(codebook.getCodebookName().equals(remoteCodebook.getCodebookName())) {
    				exists = true;
    				codebook.setRemoteGitExistanceStatus(GitCodebook.STATUS_EXISTS);
    			}
    		}
    		if(!exists) {
    			codebooks.add((GitCodebook)remoteCodebook.clone());
    		}
    	}
		long end= System.currentTimeMillis();
		logger.debug("End get all codebooks milli: " + ((end -start)));
		

    	setCodebookSynchInfo(codebooks);
	   // setRemoteCodebookValidityStatus(codebooks);
    	return codebooks;
    }

    /**
     * This method performs a diff operation between local git and remote branch.
     * Status of the codebooks is updated.
     * If the codebook is in remote but not in local, the codebook with status is added codebooks. 
     * @param codebooks List of codebooks whose status is being updated
     * @throws GitAPIException
     * @throws InvalidRemoteException
     * @throws IOException
     * @throws TransportException
     */
    private void setCodebookSynchInfo(List<GitCodebook> codebooks) throws GitAPIException,InvalidRemoteException,IOException,TransportException{
    	long start = System.currentTimeMillis();
    	logger.debug("Start Codebook Synch info");
    	Git git = null;
		try {
			git = openRepo();
       		git.fetch().
   			setCredentialsProvider(new UsernamePasswordCredentialsProvider(getRemoteUser(),getRemotePass())).
   			call();
       		setCodebookInfo(git,codebooks);
			Repository repo = git.getRepository();   
			String remoteFetchHeadStart = "origin/"+this.getRemoteBranch()+"^{tree}";
			ObjectId fetchHead = repo.resolve(remoteFetchHeadStart);
			ObjectId head = repo.resolve("HEAD^{tree}");
			ObjectReader reader = repo.newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, head);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, fetchHead);
			List<DiffEntry> diffs= git.diff()
	            .setNewTree(newTreeIter)
	            .setOldTree(oldTreeIter)
	            .call();
			for(DiffEntry entry : diffs) {
				String changeType = entry.getChangeType().name();
				
				if(changeType.equalsIgnoreCase("MODIFY")) {
					String codebookModifiedStatus = getModifiedCodebookStatus(git,entry.getNewPath().trim());
					setCodebookStatus(codebooks,entry.getNewPath().trim(),codebookModifiedStatus);
				}
				else if(changeType.equalsIgnoreCase("ADD")) {
					setCodebookStatus(codebooks,entry.getNewPath().trim(),GitCodebook.STATUS_LOCAL_NON_EXISTANT);
				}
				else if(changeType.equalsIgnoreCase("DELETE")) {
					setCodebookStatus(codebooks,entry.getOldPath().trim(),GitCodebook.STATUS_REMOTE_NON_EXISTANT);
				}
			}
	    	for(GitCodebook codebook:codebooks) {
				if(codebook.getStatus().equals(GitCodebook.STATUS_UNKNOWN)) {
	    			codebook.setStatus(GitCodebook.STATUS_UPTODATE);
	    		}
	    	}
	    	long end = System.currentTimeMillis();
	    	logger.debug("End  Codebook Synch info. Time milli sec " + (end -start));
	    	
		}
		finally {
			if(git != null) git.close();
		}
	}

    /**
     * This method pulls the codebooks from remote into local.
     * if local codebook doesn't exists in baseX ingest codebook into baseX.
     * if local codebook is behind, update baseX with latest pulled copy
     * After this method call is executed, local code book will be ahead of remote  ...
     *  remote first pull into local git, then pushed into basex. 
     *  This push to BaseX will result in creation of another version of codebook in baseX.
     *  Then, from BaseX the codebook will be pushed to local git.  
     *   
     * @throws GitAPIException
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws IOException
     */

    public void pullFromRemoteAndSynchWithBaseX() 
    throws CloneNotSupportedException,GitAPIException,InvalidRemoteException,TransportException,IOException  {
		List<GitCodebook> codebooks = getCodebookStatusInfoE();
		pullFromRemote();
		for(GitCodebook codebook:codebooks){
			if(codebook.getStatus().equalsIgnoreCase(GitCodebook.STATUS_LOCAL_BEHIND))
				addCodebookToBaseX(codebook.getCodebookBaseHandle(),codebook.getCodebookVersion(),false);	
		}
    }
    
    

    /**
     * This method replaces invalid remote codebook(s) with local codebook.
     * Pulls the remote to local, deletes the local, pushes to remote. 
     * Checks out the deleted copy and pushes the delete
     * Checks out the deleted codebook , commits the codebook and finally pushes the codebook to remote
     * @throws GitAPIException
     * @throws IOException
     */
    public void replaceRemoteCopyWithLocal() throws GitAPIException,IOException,CloneNotSupportedException{
    	List<GitCodebook> codebooks = getCodebookStatusInfo();
		for(GitCodebook codebook:codebooks) {
			if(!codebook.getStatus().equalsIgnoreCase(GitCodebook.STATUS_INVALID_REMOTE)) continue;
			this.pullFromRemote();
	    	String commitMessage = this.deleteACodebook(codebook.getCodebookName());
	    	pushToRemote();
	    	List<RevCommit> revCommits = getCommitLog(codebook.getCodebookName());
			for(int i=0; i<revCommits.size();i++) {
				String message = revCommits.get(i).getFullMessage();
				if(commitMessage.equals(message)) {
					if((i+2)<=revCommits.size()) {
						String commitId = revCommits.get(i+2).getId().getName();
						checkoutDeletedCodebook(commitId, codebook.getCodebookName());
						pushToRemote();
					}
					break;
				}
			}
		}
    }

    /**
     * This method checks out a deleted copy of the codebook as refered by the commit id.
     * @param commitId
     * @param codebookName
     * @throws GitAPIException
     * @throws IOException
     * @throws NoFilepatternException
     */
    private void checkoutDeletedCodebook(String commitId,String codebookName) 
    throws GitAPIException,IOException,NoFilepatternException{
    	Git git = null;
		try {
			git = openRepo();
			//Ref cr = 
			git.checkout()
					.setStartPoint(commitId)
					.setName(this.getLocalBranch())
					.addPath(codebookName)
					.call();
			//RevCommit rec =
			git.commit().setMessage("Checking out a deleted copy").call();
		}
		finally {
			if(git != null) git.close();
		}
    }

    /**
     * This method deletes a codebook from local reposiory and commits. Returns commit message
     * @param codebookName
     * @return
     * @throws GitAPIException
     * @throws IOException
     * @throws NoFilepatternException
     */
    private String deleteACodebook(String codebookName) throws GitAPIException,IOException,NoFilepatternException{
    	Git git = null;
    	String commitMessage = "";
		try {
			git = openRepo();
			git.rm().addFilepattern(codebookName).call();
			commitMessage = "Deleted codebook to replace local copy: " +codebookName +" at " + System.currentTimeMillis();
			git.commit().setMessage(commitMessage).call();
		}
		finally {
			if(git != null) git.close();
		}
		return commitMessage;
    }
    /**
     * This method can be called when there conflict between local and remote version of codebooks.
     * This method resolves the conflict by overriding local copy with remote copy if there is a conflict.
     * Your copy of the codebook is preserved in the local repository.
     * 
     * This method first pulls all the remote codebooks. 
     * Performs a checkout of remote copy to the stage.
     * Commit the remote copy to complete the merge in favor of remote.
     * 	
     * After this method is executed...
     * 	All the conflicted codebooks are resolved in favor of remote copies
     * 	All the local codebooks that are behind/ahead remote are updated.
     * @param preferRemote
     * @throws IOException
     * @throws GitAPIException
     */
    
    public void merge() throws IOException,GitAPIException,CloneNotSupportedException{
    	List<GitCodebook> codebooks = getCodebookStatusInfoE();
		pullFromRemote();
		Git git = null;
		try {
			git = openRepo();
			for(GitCodebook codebook:codebooks) {
				if(codebook.getStatus().equals(GitCodebook.STATUS_CONFLICT)) {
					git.checkout().setStage(Stage.THEIRS).addPath(codebook.getCodebookName()).call();
					git.add().addFilepattern(codebook.getCodebookName()).call();
					git.commit().setMessage("Merging a conflict by the remote copy as prefered copy:  " 
					+ codebook.getCodebookName()).call();
					addCodebookToBaseX(codebook.getCodebookBaseHandle(),codebook.getCodebookVersion(),false);
				}
			}	
		}
		finally {
			if(git != null) git.close();
		}
    }

    /**
     * This method pull the remote codebooks into gitRemoteCopyDirectory
     * Makes sure that the codebooks that are ahead of local copy are valid.
     * @param codebooks
     */

    private void setRemoteCodebookValidityStatus(List<GitCodebook> codebooks) throws GitAPIException, IOException{
    	pullFromRemoteIntoRemoteCopy();
    	for(GitCodebook codebook:codebooks) {
    		if(codebook.getStatus().equals(GitCodebook.STATUS_REMOTE_NON_EXISTANT)) continue;
    		
			String codebookCopy = getCodebook(codebook.getCodebookName(), true);
			if(StringUtils.isEmpty(codebookCopy)) continue; 
			InputStream is = null;
			XMLHandle xh = null;
			try {
				is = new ByteArrayInputStream(codebookCopy.getBytes());
				xh = new XMLHandle(is,Config.getInstance().getSchemaURI());
				if(!xh.isValid()) {
					codebook.setStatus(GitCodebook.STATUS_INVALID_REMOTE);
				}
			}
			//TODO: we can't blindly swallow all exceptions
			
			catch(Exception ex) {
				logger.error("Error Parsing codebook XML for codebook: " + codebook , ex);
				codebook.setStatus(GitCodebook.STATUS_INVALID_REMOTE);
			}
			finally{
				is.close();
			}
    	}
    }

    /**
	 * Pulls the codebooks from remote repository 
	 * @throws GitAPIException 
	 * @throws IOException 
	 */
	private void pullFromRemoteIntoRemoteCopy() throws GitAPIException, IOException{
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
		}
		
		long startTime = System.currentTimeMillis();
		Git git = null;			
		try{
			git = openRepo(this.getGitDirectory(gitRemoteCopyDirectory));
			git.pull()
				.setRemoteBranchName(this.getRemoteBranch())
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.getRemoteUser(),this.getRemotePass()))
				.call();
			long endTime = System.currentTimeMillis();
			logger.debug("Remote pull finished in " + (endTime-startTime)/1000.0 + " seconds");
		}finally{
			if(git != null) git.close();
		}	
	}

    /**
     * This method adds new codebook to baseX
     * @param baseHandle
     * @param version
     * @throws IOException
     */
    public int addCodebookToBaseX(String baseHandle, String version, boolean masterCopy) throws IOException{
    	String codebook = getCodebook(baseHandle+"."+version+".xml");
    	InputStream ins = IOUtils.toInputStream(codebook, "UTF-8");
    	return addCodebookToBaseX(baseHandle, version, ins,masterCopy); 
    }
    
    /**
     * This method adds new codebook to baseX
     * @param baseHandle
     * @param version
     * @throws IOException
     */
    public int  addCodebookToBaseX(String baseHandle, String version, InputStream ins, boolean masterCopy) 
    throws IOException{
		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String user = getRemoteUser();
		String baseURI= "http://localhost:"+port;
    //	Fetch.uploadCodebook(baseURI,ins,baseHandle,version,user,masterCopy);
		EditCodebookData editCodebookData = new EditCodebookData();
		return editCodebookData.postCodebook(ins, baseHandle, version, baseHandle, user, masterCopy);
    }

    /**
	 * Pulls the codebooks from remote repository 
	 * @throws GitAPIException 
	 * @throws IOException 
	 */
	public void pullFromRemote() throws GitAPIException, IOException{
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
		}
		
		long startTime = System.currentTimeMillis();
		logger.debug("Version control pull remote repository started");
		Git git = null;			
		try{
			git = openRepo();
			git.pull()
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.getRemoteUser(),this.getRemotePass()))
				.call();
			long endTime = System.currentTimeMillis();
			logger.debug("Version control push to remote repository ended, taking "+(endTime-startTime)+"ms");
		}finally{
			if(git != null) git.close();
		}	
	}
	
	/**
	 * Populates information such as last commit time, message, author etc from remote and local commits 	
	 * @param git
	 * @param codebooks
	 * @throws GitAPIException
	 * @throws NoHeadException
	 * @throws IOException
	 */
    private void setCodebookInfo(Git git,List<GitCodebook> codebooks) 
    throws GitAPIException,NoHeadException,IOException {
    	for(GitCodebook codebook:codebooks) {
			ObjectId localHead = git.getRepository().resolve("HEAD");
			Iterable<RevCommit> localLogs = git.log().add(localHead).addPath(codebook.getCodebookName()).setMaxCount(1).call();
		    for( RevCommit revCommit : localLogs ) {
		    	codebook.setLastLocalUpdateTime(revCommit.getCommitTime());
		    	codebook.setLastLocalMessage(revCommit.getFullMessage());
		    	codebook.setLastLocalAuthor(revCommit.getAuthorIdent().getName());
		    	codebook.setLastLocalCommitHash(revCommit.getName());
		    	break;
		    }
		    String remoteFetchHeadStart = "origin/"+this.getRemoteBranch();
			ObjectId remoteHead = git.getRepository().resolve(remoteFetchHeadStart);
			Iterable<RevCommit> remoteLogs = git.log().add(remoteHead).addPath(codebook.getCodebookName()).setMaxCount(1).call();
		    for( RevCommit revCommit : remoteLogs ) {
		    	codebook.setLastRemoteUpdateTime(revCommit.getCommitTime());
		    	codebook.setLastRemoteMessage(revCommit.getFullMessage());
		    	codebook.setLastRemoteAuthor(revCommit.getAuthorIdent().getName());
		    	codebook.setLastRemoteCommitHash(revCommit.getName());
		    	break;
		    }
    	}
    }
    
    /**
     * This method should be called when the diff command returns 'MODIFY' status on codebook to 
     * find if the codebook on Local GIT is ahead, behind or in conflict with the remote GIT.
     *    Local Codebook is ahead if the first Remote GIT  commit id is present in the local GIT but not the first. 
	 *    Local Codebook is behind if first Local GIT commit id is present in the  Remote GIT but not first
	 *    Local Codebook is in  Conflict with remote GIT if non of the above
     * 
     * @param git
     * @param codebookName
     * @return	Returns one of the three codebook statuses 	GitCodebook.STATUS_LOCAL_AHEAD,GitCodebook.STATUS_LOCAL_BEHIND or GitCodebook.STATUS_CONFLICT
     * @throws GitAPIException
     * @throws NoHeadException
     * @throws IOException
     */
    private String getModifiedCodebookStatus(Git git,String codebookName) throws GitAPIException, NoHeadException,IOException{
    	String codebookStatus = GitCodebook.STATUS_CONFLICT;
		ObjectId localHead = git.getRepository().resolve("HEAD");
		Iterable<RevCommit> localLogs = git.log().add(localHead).addPath(codebookName).call();
		String remoteRepoStart = "origin/"+this.getRemoteBranch();
		ObjectId remoteHead = git.getRepository().resolve(remoteRepoStart);
		Iterable<RevCommit> remoteLogs = git.log().add(remoteHead).addPath(codebookName).call();
		
    	boolean localAhead = false;
    	String firstRemoteCommitId="";
	    for( RevCommit revCommit : remoteLogs ) {
	    	firstRemoteCommitId = revCommit.getId().getName();
	    	break;
	    }
	    int commitCount=0;
	    boolean commitFoundInLocal=false;
	    for( RevCommit revCommit : localLogs ) {
	    	commitCount++;
	    	if(firstRemoteCommitId.equals(revCommit.getId().getName())) {
	    		commitFoundInLocal=true;
	    		break;
	    	}
	    }
	    if(commitFoundInLocal&&commitCount>1) {
	    	localAhead=true;
	    	codebookStatus = GitCodebook.STATUS_LOCAL_AHEAD;
	    }
	    
	    if(!localAhead) {
			localLogs = git.log().add(localHead).addPath(codebookName).call();
			remoteLogs = git.log().add(remoteHead).addPath(codebookName).call();

	    	String firstLocalCommitId="";
		    for( RevCommit revCommit : localLogs ) {
		    	firstLocalCommitId = revCommit.getId().getName();
		    	break;
		    }
	
		    commitCount=0;
		    boolean commitFoundInRemote=false;
		    for( RevCommit revCommit : remoteLogs ) {
		    	commitCount++;
		    	if(firstLocalCommitId.equals(revCommit.getId().getName())) {
		    		commitFoundInRemote=true;
		    		break;
		    	}
		    }
		    if(commitFoundInRemote&&commitCount>1) {
		    	codebookStatus = GitCodebook.STATUS_LOCAL_BEHIND;
		    }
	    }
    	return codebookStatus;
    }
    


    /**
     * This is a utility method that updates status of the codebook in the list.
     * If the codebook is not in the list, new is added with status of GitCodebook.STATUS_LOCAL_NON_EXISTANT
     * @param codebooks
     * @param codebookName
     * @param status
     */
    private static void setCodebookStatus(List<GitCodebook> codebooks, String codebookName, String status) {
    	for(GitCodebook codebook:codebooks) {
    		if(codebook.getCodebookName().equals(codebookName)) {
    			codebook.setStatus(status);
    		}
    	}
    }   

    /**
     * This is a utility method that returns passed file name is codebook or not.
     * A file with an extension .xml is considered possible codebook
     * codebook name should be in the format of ssb.v1.xml or ssb.1.xml for ssb version 1 
     * @param fileName
     * @return
     */
    private static boolean isCodebook(String fileName) {
    	boolean codebook=false;
    	if(!fileName.endsWith(".xml")) { 
    		codebook = false;
    	}else{
    		String codebookNameNoExt  = fileName.substring(0,(fileName.length()-4));
    		String splits[] = codebookNameNoExt.split("\\.");
    		if(splits.length == 2) {
    			codebook = true;
    		}
    	}
    	return codebook;
    }

    /**
     * This method return list of available code books in the local repository.
     * This method does not perform commit operation before fetching the codebooks.
     * All the codebooks will have a status of unknown since diff operation with remote 
     * is not performed yet
     * @return List of codebooks that are  committed.  
     * @throws IOException
     */
    public List<GitCodebook> getCodebooksInLocalRepository() 
    throws IOException{
    	List<GitCodebook> codebooks = new ArrayList<GitCodebook>();
		Git git = null;		
		TreeWalk treeWalk = null;
		RevWalk walk = null;
		try {
			git = openRepo();
	    	Repository repository = git.getRepository();
	        Ref head = repository.getRef("HEAD");
	        walk = new RevWalk(repository);
	        // if walk is null, there are no code books in local repository.
	        if(head.getObjectId() != null) {  
		        RevCommit commit = walk.parseCommit(head.getObjectId());
		        RevTree tree = commit.getTree();
		        treeWalk = new TreeWalk(repository);
		        treeWalk.addTree(tree);
		        treeWalk.setRecursive(false);
		        while (treeWalk.next()) {
		        	if(isCodebook(treeWalk.getNameString())) {
		        		GitCodebook codebook = new GitCodebook();
		        		codebook.setCodebookName(treeWalk.getNameString());
		        		codebook.setStatus(GitCodebook.STATUS_UNKNOWN);
		        		codebook.setLocalGitExistanceStatus(GitCodebook.STATUS_EXISTS);
		        		codebooks.add(codebook);
		        	}
		        }
			}
		}
		finally {
			if(git != null) git.close();
			if(treeWalk != null) treeWalk.close();
			if(walk != null) walk.close();
		}
        return codebooks;
    }
    
    /**
     * This method return list of available code books in the local repository.
     * This method does not perform commit operation before fetching the codebooks.
     * All the codebooks will have a status of unknown since diff operation with remote 
     * is not performed yet
     * @return List of codebooks that are  committed.  
     * @throws IOException
     */
    public Map<String,GitCodebook> getCodebooksInLocalRepository(Map<String,GitCodebook> codebooks) 
    throws IOException{
		Git git = null;		
		TreeWalk treeWalk = null;
		RevWalk walk = null;
		try {
			git = openRepo();
	    	Repository repository = git.getRepository();
	        Ref head = repository.getRef("HEAD");
	        walk = new RevWalk(repository);
	        // if walk is null, there are no code books in local repository.
	        if(head.getObjectId() != null) {  
		        RevCommit commit = walk.parseCommit(head.getObjectId());
		        RevTree tree = commit.getTree();
		        treeWalk = new TreeWalk(repository);
		        treeWalk.addTree(tree);
		        treeWalk.setRecursive(false);
		        while (treeWalk.next()) {
		        	if(isCodebook(treeWalk.getNameString())) {
		        		GitCodebook codebook = new GitCodebook();
		        		codebook.setCodebookName(treeWalk.getNameString());
		        		codebook.setStatus(GitCodebook.STATUS_UNKNOWN);
		        		codebook.setLocalGitExistanceStatus(GitCodebook.STATUS_EXISTS);
		        		if(!codebooks.containsKey(treeWalk.getNameString()))
		        				codebooks.put(treeWalk.getNameString(),codebook);
		        	}
		        }
			}
		}
		finally {
			if(git != null) git.close();
			if(treeWalk != null) treeWalk.close();
			if(walk != null) walk.close();
		}
        return codebooks;
    }

    /**
     * Adds codebooks from local master into BaseX
     * is not performed yet
     * @return List of codebooks that are  committed.  
     * @throws IOException
     */
    private void addCodebooksInLocalMaster(){
		Git git = null;		
		TreeWalk treeWalk = null;
		RevWalk walk = null;
		try {
			//git = openRepo("gitMaster");
			git = openRepo(getGitDirectory(gitMasterDirectory));
	    	Repository repository = git.getRepository();
	        Ref head = repository.getRef("HEAD");
	        walk = new RevWalk(repository);
	        if(head.getObjectId() != null) {  
		        RevCommit commit = walk.parseCommit(head.getObjectId());
		        RevTree tree = commit.getTree();
		        treeWalk = new TreeWalk(repository);
		        treeWalk.addTree(tree);
		        treeWalk.setRecursive(false);
		        while (treeWalk.next()) {
		        	if(isCodebook(treeWalk.getNameString())) {
		        		String[] names = treeWalk.getNameString().split("\\.");
		        		ObjectId objectId = treeWalk.getObjectId(0);
		     		    ObjectLoader loader = repository.open(objectId);
		     		    ByteArrayOutputStream out  = null;
		     		    InputStream in = null;
		     		    try{
		     		    	out = new ByteArrayOutputStream();
		     		    	loader.copyTo(out);
		     		    	String outString = out.toString();
		     		    	in = IOUtils.toInputStream(outString, "UTF-8");
		     			    addCodebookToBaseX(names[0], names[1], in, true);
		     		    }finally{
		     		    	if(out != null) out.close();
		     		    	if(in != null) in.close();
		     		    }
		        	}
		        }
			}
		}catch(IOException e){
			e.printStackTrace();
			logger.error("Could not read from local master copy");
		}
		finally {
			if(git != null) git.close();
			if(treeWalk != null) treeWalk.close();
			if(walk != null) walk.close();
		}
    }

    /**
     * This method fetches codebooks in the remote repository. It performs a fetch operation before looking
     * for the codebooks, then it gets the list of codebooks from that fetch head.
     * @return List of codebooks in the remote repository
     * @throws IOException
     * @throws TransportException
     * @throws InvalidRemoteException
     * @throws GitAPIException
     */
    public List<GitCodebook> getCodebooksInRemoteRepository() 
    throws IOException,TransportException,InvalidRemoteException,GitAPIException{
    	List<GitCodebook> codebooks = new ArrayList<GitCodebook>();
		Git git = null;
		TreeWalk treeWalk = null;
		RevWalk walk = null;
		try {
			git = openRepo();
	    	Repository repository = git.getRepository();
	    	//FetchResult fr = 
       		git.fetch()
       		.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getRemoteUser(),getRemotePass()))
       		.call();
       		
            walk = new RevWalk(repository);
            ObjectId objectId = git.getRepository().resolve("origin/"+this.getLocalBranch());
            if(objectId ==null) return codebooks; // empty remote
            RevCommit commit = walk.parseCommit(objectId);
            RevTree tree = commit.getTree();
            treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
            	if(isCodebook(treeWalk.getNameString())) {
	        		GitCodebook codebook = new GitCodebook();
	        		codebook.setCodebookName(treeWalk.getNameString());
	        		codebook.setStatus(GitCodebook.STATUS_UNKNOWN);
	        		codebook.setRemoteGitExistanceStatus(GitCodebook.STATUS_EXISTS);
	        		codebooks.add(codebook);
            	}
            }
		}finally {
			if(git != null) git.close();
			if(treeWalk != null) treeWalk.close();
			if(walk != null) walk.close();
		}
		return codebooks;
    }

    /**
     * This method fetches codebooks in the remote repository. It performs a fetch operation before looking
     * for the codebooks, then it gets the list of codebooks from that fetch head.
     * @return Map of codebooks in the remote repository
     * @throws IOException
     * @throws TransportException
     * @throws InvalidRemoteException
     * @throws GitAPIException
     */
    public Map<String,GitCodebook> getCodebooksInRemoteRepository(Map<String,GitCodebook> codebooks) 
    throws IOException,TransportException,InvalidRemoteException,GitAPIException{
		Git git = null;
		TreeWalk treeWalk = null;
		RevWalk walk = null;
		try {
			git = openRepo();
	    	Repository repository = git.getRepository();
	    	//FetchResult fr = 
       		git.fetch()
       		.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getRemoteUser(),getRemotePass()))
       		.call();
       		
            walk = new RevWalk(repository);
            ObjectId objectId = git.getRepository().resolve("origin/"+this.getLocalBranch());
            if(objectId ==null) return codebooks; // empty remote
            RevCommit commit = walk.parseCommit(objectId);
            RevTree tree = commit.getTree();
            treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
            	if(isCodebook(treeWalk.getNameString())) {
	        		GitCodebook codebook = new GitCodebook();
	        		codebook.setCodebookName(treeWalk.getNameString());
	        		codebook.setStatus(GitCodebook.STATUS_UNKNOWN);
	        		codebook.setRemoteGitExistanceStatus(GitCodebook.STATUS_EXISTS);
	        		if(codebooks.containsKey(treeWalk.getNameString())) {
	        			GitCodebook remoteCodebook =codebooks.get(treeWalk.getNameString()); 
	        			remoteCodebook.setRemoteGitExistanceStatus(GitCodebook.STATUS_EXISTS);
	        		}
	        		else {	
	        			codebooks.put(treeWalk.getNameString(),codebook);
	        		}
            	}
            }
		}finally {
			if(git != null) git.close();
			if(treeWalk != null) treeWalk.close();
			if(walk != null) walk.close();
		}
		return codebooks;
    }


    
    /**
     * This method returns codebooks in the BaseX as a List 
     * @return
     */
    private List<GitCodebook> getCodebooksInBaseX() {
    	List<GitCodebook> codebooks = new ArrayList<GitCodebook>();
		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String appName = this.webAppName;
		if(context != null) {
			appName = context.getContextPath();
		}
		String baseURI= "http://localhost:"+port+"/"+appName+"/rest/";
		Collection<String[]> codebooksInBaseX = new ArrayList<String[]>();
		try {
			codebooksInBaseX = Fetch.getCodebooks(baseURI).values();
			for(String[] codebook:codebooksInBaseX) {
				GitCodebook cb= new GitCodebook();
				String codebookName = codebook[0]+"."+codebook[1]+".xml";
				cb.setCodebookName(codebookName);
				cb.setStatus(GitCodebook.STATUS_UNKNOWN);
				cb.setBaseXExistanceStatus(GitCodebook.STATUS_EXISTS);
				codebooks.add(cb);
			}
		}
		catch(Exception ex) {
			logger.error("Version control class could not find any codebooks in BaseX " + ex);
		}
		return codebooks;
    }

    /**
     * This method returns codebooks in the BaseX as a Map 
     * @return
     */
    private Map<String,GitCodebook> getCodebooksInBaseX(Map<String,GitCodebook> codebooks) {
		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String appName = this.webAppName;
		if(context != null) {
			appName = context.getContextPath();
		}
		String baseURI= "http://localhost:"+port+"/"+appName+"/rest/";
		Collection<String[]> codebooksInBaseX = new ArrayList<String[]>();
		try {
			codebooksInBaseX = Fetch.getCodebooks(baseURI).values();
			for(String[] codebook:codebooksInBaseX) {
				GitCodebook cb= new GitCodebook();
				String codebookName = codebook[0]+"."+codebook[1]+".xml";
				cb.setCodebookName(codebookName);
				cb.setStatus(GitCodebook.STATUS_UNKNOWN);
				cb.setBaseXExistanceStatus(GitCodebook.STATUS_EXISTS);
				if(!codebooks.containsKey(codebookName))
					codebooks.put(codebookName,cb);
			}
		}
		catch(Exception ex) {
			logger.error("Version control class could not find any codebooks in BaseX " + ex);
		}
		return codebooks;
    }

    
    //Getters and setter methods		
	public String getRemoteRepoURL() {
		return remoteRepoURL;
	}

	public String getRemoteBranch() {
		return remoteBranch;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public String getRemotePass() {
		return remotePass;
	}

	public String getLocalBranch(){
		return localBranch;
	}
	

	public boolean isGitEnabled() {
		return isGitEnabled;
	}
	public void setGitEnabled(boolean isGitEnabled) {
		this.isGitEnabled = isGitEnabled;
		if(isGitEnabled){
			ConfigurationProperties CP = new ConfigurationProperties();
			this.remoteRepoURL = CP.getValue("remoteRepoURL");
			this.remoteBranch = CP.getValue("remoteBranch");
			this.remoteUser = CP.getValue("remoteUser");
			this.remotePass = CP.getValue("remotePass");
			this.localBranch = CP.getValue("localBranch");
			this.numberOfCommitsToPushRemote = Integer.parseInt(CP.getValue("numberOfCommitsToPushRemote"));
		}
	}

	public int getNumberOfCommitsToPushRemote() {
		return numberOfCommitsToPushRemote;
	}	
}