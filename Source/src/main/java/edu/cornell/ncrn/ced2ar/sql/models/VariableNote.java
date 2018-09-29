package edu.cornell.ncrn.ced2ar.sql.models;

public class VariableNote {
	
	private String name;
	private String codebookID;
	private int id;
	private String value;

/*Getters and setters*/	
	public String getName(){
		return name;
	}
	
	public void setName(String s){
		name = s;
	}
	
	public String getCodebookID(){
		return codebookID;
	}
	
	public void setCodebookID(String s){
		codebookID = s;
	}
	
	public int getID(){
		return id;
	}
	
	public void setID(int i){
		id = i;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setValue(String s){
		value = s;
	}
}