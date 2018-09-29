package edu.cornell.ncrn.ced2ar.sql.models;

public class VariableValue {
	
	private String name;
	private String codebookID;
	private String value;
	private String valueLabel;

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
	
	public String getValue(){
		return value;
	}
	
	public void setValue(String s){
		value = s;
	}
	
	public String getValueLabel(){
		return valueLabel;
	}
	
	public void setValueLabel(String s){
		valueLabel = s;
	}
}