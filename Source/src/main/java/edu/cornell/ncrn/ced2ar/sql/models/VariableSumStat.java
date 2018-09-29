package edu.cornell.ncrn.ced2ar.sql.models;

public class VariableSumStat {
	
	private String name;
	private String codebookID;
	private String stat;
	private double statValue;

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
	
	public String getStat(){
		return stat;
	}
	
	public void setStat(String s){
		stat = s;
	}
	
	public double getStatValue(){
		return statValue;
	}
	
	public void setStatValue(double d){
		statValue = d;
	}
}