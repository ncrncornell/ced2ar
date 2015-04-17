package edu.ncrn.cornell.ced2ar.sql.models;

public class Variable {
	
	private String name;
	private String codebookID;
	private String label;
	private int endPos;
	private int startPos;
	private String originalName;
	
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
	public String getLabel(){
		return label;
	}
	
	public void setlabel(String s){
		label = s;
	}
	
	public String getOriginalName(){
		return originalName;
	}
	
	public void setOriginalName(String s){
		originalName = s;
	}	
	
	public int getStartPos(){
		return startPos;
	}
	
	public void setStartPos(int i){
		startPos = i;
	}
	
	public int getEndPos(){
		return endPos;
	}
	
	public void setEndPos(int i){
		endPos = i;
	}	
}