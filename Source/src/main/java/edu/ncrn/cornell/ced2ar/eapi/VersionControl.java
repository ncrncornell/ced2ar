package edu.ncrn.cornell.ced2ar.eapi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;

/**
 *Prepackaged queries and preparped functions that access BaseX and PgSQL
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
	
	private boolean loadedBaseX = false;
	private String remoteRepoURL;
	private String remoteBranch;
	private String remoteUser;
	private String remotePass;
	private String localBranch;
	private boolean isGitEnabled;
	private int numberOfCommitsToPushRemote;
	private final String branchPrefix = "refs/heads/";	
	
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
		
		try{
			logger.debug("Commit repo task called");
			long startTime = System.currentTimeMillis();	
			
			//Loads current codebooks from BaseX once
			//TODO: might want to make optional
			if(BaseX.testConnection() && !loadedBaseX){
				logger.debug("Checking BaseX for unstaged codebooks...");
				Config config = Config.getInstance();
				String port = Integer.toString(config.getPort());
				String webAppName = context.getContextPath();
				String baseURI= "http://localhost:"+port+webAppName+"/rest/";
				fillRepoFromBaseX(baseURI);			
				loadedBaseX = true;
				logger.debug("Done checking BaseX.");
			}else{
				logger.debug("Version control commit to local repository started");
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
		}catch(IOException|GitAPIException e){
			logger.error("Error commiting: "+e.getMessage());
			e.printStackTrace();
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
	 * Writes commit info to BaseX DB
	 */
	private void recordCommit(RevCommit commit){
		logger.debug("recording commit...");
		Git git = null;
		Repository repo = null;	
		try{
			git = openRepo();
			repo = git.getRepository();
			RevWalk rw = new RevWalk(repo);
			RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(repo);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
			String timestamp = Integer.toString(commit.getCommitTime());
			String hash =commit.getId().getName();
			List<String> handles = new ArrayList<String>();
			logger.debug(hash +" files changed:");
			for(DiffEntry diff : diffs) {
				try{
					String fileName = diff.getNewPath();
					String handle = fileName.substring(0, fileName.lastIndexOf(".")).replace(".", "");
					handles.add(handle);
					logger.debug(fileName + " "+handle);	
				}catch(StringIndexOutOfBoundsException e){
					logger.error(e.getMessage());
				}
			}
			String shortMessage = commit.getShortMessage();
			Pattern pattern = Pattern.compile("\\{(.+?)?\\}");
			Matcher matcher = pattern.matcher(shortMessage);
			Hashtable<String,List<String>> commitVars = new Hashtable<String,List<String>>();
			while (matcher.find()) {
			   String[] data = matcher.group(1).split(",");
			   try{
				   if(data[1].equals("var")){
					   String handle = data[0];
					   String name = data[2];
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
					   } 
				   }
			   }catch(ArrayIndexOutOfBoundsException e){
				   logger.error(e.getMessage());
			   }
			}			
			QueryUtil.insertCommit(hash, timestamp, handles);
			QueryUtil.insertVarCommit(hash, commitVars);
			
		}catch (IOException|NullPointerException e) {
			e.printStackTrace();
		}finally{
			if(git != null) git.close();
			if(repo != null) repo.close();
		}
	}

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
		    Set changedSet = status.getChanged();
		    Set addedSet = status.getAdded();
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
	 * Returns a String that represents latest committed version of codebook.
	 * If there is no codebook in the repository, an empty string is returned    
	 * @param  codebookName  Name of the Codebook 
	 * @return contents of the Codebook as a String
	 * @throws IOException 
	 */	
	private String getCodebook(String codebookName) throws IOException{
		//TODO:Method will be made public once the versioning is exposed to the user
		Git git = null;
		Repository repo = null;
		String codebookAsString  = "";
		try{
		    git = openRepo();
		    repo = git.getRepository();
		    ObjectId lastCommitId = repo.resolve(Constants.HEAD);
		    RevWalk revWalk = new RevWalk(repo);
		    RevCommit commit = revWalk.parseCommit(lastCommitId);
		    codebookAsString = getCodebook(codebookName,  repo, commit);
		}finally{
			if(repo !=null)repo.close();
			if(git!=null)git.close();
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
		try{
			git = openRepo();
		    repo = git.getRepository();
		    ObjectId lastCommitId = repo.resolve(Constants.HEAD);
		    RevWalk revWalk = new RevWalk(repo);
		    RevCommit commit = revWalk.parseCommit(lastCommitId);//TODO: still occasionally causes nullpointer
		    RevTree tree = commit.getTree();
		    TreeWalk treeWalk = new TreeWalk(repo);
		    treeWalk.addTree(tree);
		    treeWalk.setRecursive(true);
		    while(treeWalk.next()){
		    	logger.debug("found codebook in repo: "+treeWalk.getPathString());
		    	names.add(treeWalk.getPathString());
		    }
		}finally{
			if(repo != null) repo.close();
			if(git != null) git.close();
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
			file = new File(getGitWorkingDirectory(), fileName);
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
			File file = new File(getGitWorkingDirectory(), fileName);
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
			 RevWalk walk = new RevWalk(repo);
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
			 
			 if(count >= getNumberOfCommitsToPushRemote()){
				for(RevCommit pendingCommit : pendingCommits){
					recordCommit(pendingCommit);
				}	
			}
		}catch(IOException|GitAPIException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(repo != null) repo.close();
			if(git != null) git.close();
		}
		return count;
	}

	/**
	 * Gets the list of commits from the log
	 * can be turned public once versioning is exposed to the user
	 * @return List of RevCommit objects.
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws Exception
	 */
	protected List<RevCommit> getCommitLog() throws  IOException, GitAPIException  {
		 Iterable<RevCommit> log;
		 Git git  = null;
		 Repository repo = null;
		 ArrayList<RevCommit> commitLog = new ArrayList<RevCommit>();
		 try{	 
			 git = openRepo();
			 log = git.log().call();
			 Iterator<RevCommit> i = log.iterator();
			 while(i.hasNext()){
				 RevCommit rc = (RevCommit)i.next();
				 repo = git.getRepository();
				 ObjectLoader loader = repo.open(rc.getId());
				 loader.copyTo(System.out);
				 commitLog.add(rc);
			 }
		 }catch(NullPointerException e){
			 logger.debug("Commit log is empty");
			 e.printStackTrace();
		 }
		 finally{
			 if(repo!=null) repo.close();
			 if(git !=null) git.close(); 
		 }
		 return commitLog;
	 }

	/**
	 * boolean test to see if there exists a GIT repo in the working directory provided
	 * @return a boolean whether or not the directory has a git repo
	 */
	public boolean hasLocalRepo(){
		String gitLocation = getGitWorkingDirectory()+"/.git";
		if(RepositoryCache.FileKey.isGitRepository(new File(gitLocation), FS.DETECTED))
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
		File localPath = new File(getGitWorkingDirectory());
        //localPath.delete();
        Repository repository = null;       
        try{
	        // then clone
        	logger.debug("Cloning " + remoteRepoURL);
	        Git.cloneRepository()
	            .setURI(remoteRepoURL)
	            .setDirectory(localPath)
	            .setBranch(branchPrefix+remoteBranch) // refs/heads/mastercodebooks
	            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(remoteUser,remotePass))
	            .call();
	
	        // now open the created repository
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
	private void pushToRemote() throws GitAPIException, IOException{
		if(!isGitEnabled()){
			logger.debug("Version control is not enabled. Exiting.");
			return;
		}
		
		long startTime = System.currentTimeMillis();
		logger.debug("Version control push to remote repository started");
		Git git = null;			
		try{
			RefSpec ref = new RefSpec(branchPrefix+remoteBranch+":"+branchPrefix+remoteBranch);
			git = openRepo();
			git.push()
				.setRemote(remoteRepoURL)
				.setRefSpecs(ref)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.getRemoteUser(),this.getRemotePass()))
				.call();
			
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
		boolean newCodebooks = false;
		try {
			List<String> current = getCodebooks();
			Collection<String[]> codebooks = Fetch.getCodebooks(baseURI).values();
			for(String[] codebook : codebooks){
				String handle = codebook[0]+codebook[1];
				String fileName = codebook[0]+"."+codebook[1]+".xml";
				String contents = Fetch.get(baseURI+"codebooks/"+handle+"?type=git");//getXML decodes ampersands
				if(!current.contains(fileName)){
					logger.debug("Found new codebook: "+fileName);
					newCodebooks = true;	
					stageCodebook(fileName, contents,".");
				}
			}
			if(newCodebooks){
				commit("Commiting codebooks retrieved directly from BaseX");
			}
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
		//TODO: Keeps throwing false positive for repoNotFoundException
		Git git = Git.open(new File(getGitWorkingDirectory()));
		return git;
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
	 * @throws Exception
	 */
	private String getCodebook(String codebookName, Repository repo, RevCommit commit) 
	throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException{
		String codebook="";
		ByteArrayOutputStream out  = null;
		RevTree tree = commit.getTree();
	    TreeWalk treeWalk = new TreeWalk(repo);
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
	    return codebook;
	}

	/**
	 * Translates BaseX file names,ie ssb6, into repo format, ie ssb.6.xml
	 * @param s
	 * @return
	 */
	private String toRepoFormat(String s){
		String temp = s.replaceAll(".xml", "");
		String[] handles = QueryUtil.getFullHandles();
		for(int i = 0; i < handles.length; i++){
			if(handles[i].replace(".", "").equals(temp))
				return handles[i]+".xml";
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
	 *Retrieves path to git directory
	 *@return absolute path of the git repository
	 */
	//TODO:When context is not null, git working directory is extracted from the context
	//When context is null, the git directory is extracted from the url
	public String getGitWorkingDirectory() {
		String realGitDirectory ="";
		if(context == null){ 
			URL url = VersionControl.class.getResource("VersionControl.class");
			String className = url.getFile();
			String webInfPath = className.substring(0,className.indexOf("WEB-INF") + "WEB-INF".length());
			realGitDirectory=webInfPath+"/"+gitWorkingDirectory+"/"; 
		}
		else{
			realGitDirectory = context.getRealPath("/WEB-INF/"+gitWorkingDirectory+"/");
		}
		File dir = new File(realGitDirectory);
		if(!dir.exists()){
			dir.mkdir();
		}
		return realGitDirectory;		
	}
	
//Getters and setter methods		
	public String getRemoteRepoURL() {
		return remoteRepoURL;
	}

	public void setRemoteRepoURL(String remoteRepoURL) {
		this.remoteRepoURL = remoteRepoURL;
	}

	public String getRemoteBranch() {
		return remoteBranch;
	}

	public void setRemoteBranch(String remoteBranch) {
		this.remoteBranch = remoteBranch;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public String getRemotePass() {
		return remotePass;
	}

	public void setRemotePass(String remotePass) {
		this.remotePass = remotePass;
	}
	
	public String getLocalBranch(){
		return localBranch;
	}
	
	public void setLocalBranch(String localBranch){
		this.localBranch = localBranch;
	}

	public boolean isGitEnabled() {
		return isGitEnabled;
	}
	public void setGitEnabled(boolean isGitEnabled) {
		this.isGitEnabled = isGitEnabled;
	}

	public int getNumberOfCommitsToPushRemote() {
		return numberOfCommitsToPushRemote;
	}
	
	public void setNumberOfCommitsToPushRemote(int numberOfCommitsToPushRemote) {
		this.numberOfCommitsToPushRemote = numberOfCommitsToPushRemote;
	}
}