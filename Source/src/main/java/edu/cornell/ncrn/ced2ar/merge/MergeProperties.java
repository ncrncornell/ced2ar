package edu.cornell.ncrn.ced2ar.merge;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.cornell.ncrn.ced2ar.api.data.Config;

public class MergeProperties implements Serializable {
	
	private static final long serialVersionUID = 73187675;
	private static String[] codebooks = null;
	
	//Instantiated fields 
	private String baseHandle;
	private String version;
	
	private String localDDI;
	private String remoteDDI;
	
	private String localSnippet;
	private String remoteSnippet;
	
	private Set<String> sharedVars;
	private Set<String> uniqueRemoteVars;
	private Set<String> uniqueLocalVars;
	
	private LinkedList<String> diffVars;
	private String currentVar;
	
	//Static mutators
	public String getRemoteRepo(){
		return Config.getInstance().getRemoteURL();
	}
	
	protected String getLocalRepo(){
		//"https://demo.ncrn.cornell.edu/ced2ar-web";
		//"https://dev.ncrn.cornell.edu/ced2ar-web";
		return "http://localhost:"+Config.getInstance().getPort()+"/ced2ar-web";
	}
	
	public static String[] getCodebooks(){
		return codebooks;
	}
	
	public static void setCodebooks(String[] c){
		codebooks = c;
	}
	
	//Non-static mutators
	public String getBaseHandle(){
		return baseHandle;
	}
	
	protected void setBaseHandle(String h){
		baseHandle = h;
	}
	
	public String getVersion(){
		return version;
	}
	
	protected void setVersion(String v){
		version = v;
	}
	
	public String getHandle(){
		return baseHandle+version;
	}
	
	
	public Set<String> getSharedVars(){
		return sharedVars;
	}
	
	public void setSetSharedVars(Set<String> s){
		sharedVars = s;
	}
	
	public Set<String> getUniqueRemoteVars(){
		return uniqueRemoteVars;
	}
	
	public void setUniqueRemoteVars(Set<String> s){
		uniqueRemoteVars = s;
	}
	
	public Set<String> getUniqueLocalVars(){
		return uniqueLocalVars;
	}
	
	public void setUniqueLocalVars(Set<String> s){
		uniqueLocalVars = s;
	}
	
	public List<String> getDiffVars(){
		return diffVars;
	}
	
	protected void setDiffVars(LinkedList<String> l){
		diffVars = l;
	}	
	
	protected String popDiffVars(){
		String current = diffVars.pop();
		setCurrentVar(current);
		return current;
	}	
	
	public String getCurrentVar(){
		return currentVar;
	}
	
	private void setCurrentVar(String s){
		currentVar = s;
	}	
	
	protected String getLocalDDI(){
		return localDDI;
	}
	
	protected void setLocalDDI(String s){
		localDDI = s;
	}
	
	protected String getRemoteDDI(){
		return remoteDDI;
	}
	
	protected void setRemoteDDI(String s){
		remoteDDI = s;
	}
	
	public String getLocalSnippet(){
		return localSnippet;
	}
	
	protected void setLocalSnippet(String s){
		localSnippet = s;
	}
	
	public String getRemoteSnippet(){
		return remoteSnippet;
	}
	
	protected void setRemoteSnippet(String s){
		remoteSnippet = s;
	}
}