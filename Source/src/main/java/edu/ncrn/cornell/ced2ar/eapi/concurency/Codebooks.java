package edu.ncrn.cornell.ced2ar.eapi.concurency;

import java.util.HashMap;
import java.util.Map;

public class Codebooks {
	private static Map<String,Codebook> _localCodebooks = new HashMap<String,Codebook>();
	
	public static Map<String,Codebook> getCodebooks(){
		return _localCodebooks;
	}
	
	public static void setCodebooks(Map<String,Codebook> c){
		_localCodebooks = c;		
	}
	
	public static Codebook getCodebook(String handle){
		return _localCodebooks.get(handle);
	}
	
	public static void addCodebook(Codebook c){
		String handle = c.getHandle();
		_localCodebooks.put(handle, c);
	}
	
	public static void addCodebook(String baseHandle, String version){
		String handle = baseHandle + version;
		Codebook codebook = new Codebook(baseHandle, version);
		_localCodebooks.put(handle, codebook);
	}
		
	public static void removeCodebook(Codebook c){
		String handle = c.getHandle();
		removeCodebook(handle);
	}
	
	public static void removeCodebook(String handle){
		_localCodebooks.remove(handle);
	}
	
	public static boolean hasCodebook(String handle){
		return _localCodebooks.containsKey(handle);
	}
}