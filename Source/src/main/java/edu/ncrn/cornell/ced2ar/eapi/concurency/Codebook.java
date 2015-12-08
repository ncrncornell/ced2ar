package edu.ncrn.cornell.ced2ar.eapi.concurency;

import java.util.HashMap;
import java.util.Map;

public class Codebook {
	private String _baseHandle;
	private String _version;
	private Map<String,Field>_pendingChanges = new HashMap<String,Field>();
	
	public Codebook(String baseHandle, String version){
		_baseHandle = baseHandle;
		_version = version;		
	}
	
	public String getHandle(){
		return _baseHandle + _version;
	}
	
	public boolean hasPendingChange(String xpath){
		if(_pendingChanges.containsKey(xpath)){
			Field field = _pendingChanges.get(xpath);
			return field.pendingChangesCount() > 0 ? true : false;
		}		
		return false;
	}
	
	public int pendingChanges(String xpath){
		if(_pendingChanges.containsKey(xpath)){
			Field field = _pendingChanges.get(xpath);
			return field.pendingChangesCount();
		}		
		return 0;
	}
	
	public void putPendingChange(String xpath, Field field){
		_pendingChanges.put(xpath, field);			
	}
	
	
	
	public Field getField(String xpath){
		return _pendingChanges.get(xpath);
	}
}