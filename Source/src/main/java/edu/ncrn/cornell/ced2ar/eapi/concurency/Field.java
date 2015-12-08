package edu.ncrn.cornell.ced2ar.eapi.concurency;

import java.util.HashMap;
import java.util.Map;

public class Field {
	private String _xpath;
	//private LinkedList<FieldState> _fieldStates = null;
	private Map<String,FieldState> _fieldStates = null;
	
	public Field(String xpath){
		_xpath = xpath;
		_fieldStates = new HashMap<String,FieldState>();
	}

	public String getXpath(){
		return _xpath;
	}
	
	public void setXpath(String p){
		_xpath = p;
	}
	
	public Map<String,FieldState> getFieldStates(){
		return _fieldStates;
	}
	
	public int pendingChangesCount(){
		return _fieldStates.size();
	}
	
	public void addFieldState(String uid, FieldState state){
		_fieldStates.put(uid, state);
	}

	public FieldState getFieldState(String uid){
		return _fieldStates.get(uid);
	}
	
	public void removeFieldState(String uid){
		_fieldStates.remove(uid);
	}
}