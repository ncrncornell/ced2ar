package edu.ncrn.cornell.ced2ar.sql.models;

public class Codebook {
	
	private String id;
	private String handle;
	private String version;

/*Getters and setters*/	
	public String getID(){
		return id;
	}
	
	public void setID(String s){
		id = s;
	}
	
	public String getHandle(){
		return handle;
	}
	
	public void setHandle(String s){
		handle = s;
	}
	public String getVersion(){
		return version;
	}
	
	public void setVersion(String s){
		version = s;
	}
}