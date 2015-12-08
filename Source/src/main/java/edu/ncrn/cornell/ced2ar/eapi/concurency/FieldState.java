package edu.ncrn.cornell.ced2ar.eapi.concurency;

public class FieldState {
	private String _uID;
	private String _fieldValue;
	private String _user;
	private long _timeStamp;
	
	public FieldState(String sessionID, String fieldValue, String user, long timeStamp){
		setUID(sessionID);
		setFieldValue(fieldValue);
		setUser(user);
		setTimeStamp(timeStamp);
	}
	
	public FieldState(String sessionID, String fieldValue, String user){
		setUID(sessionID);
		setFieldValue(fieldValue);
		setUser(user);
		setTimeStamp();
	}
	
//Mutators	
	public String getUID(){
		return _uID;
	}
	
	public void setUID(String s){
		_uID = s;
	}
	
	public String getFieldValue(){
		return _fieldValue;
	}
	
	public void setFieldValue(String v){
		_fieldValue = v;
	}
	
	public String getUser(){
		return _user;
	}
	
	public void setUser(String u){
		_user = u;
	}
	
	public long getTimeStamp(){
		return  _timeStamp;
	}
	
	public void setTimeStamp(long t){
		_timeStamp = t;
	}
	
	public void setTimeStamp(){
		_timeStamp = System.currentTimeMillis();
	}
}