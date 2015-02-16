package edu.ncrn.cornell.ced2ar.web.classes;

/**
 *Transforms Advanced Search Input into fields that the Search Controller can parse 
 *@author Ben Perry
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class AdvancedParse {

	/**
	 * Method main. Sets control flow for the type of search that is specified
	 * @param input String the query string
	 * @param type String the type of query; any, all or none
	 * @return String the parsed query
	 */
	public static String main(String input, String type){
		String out = "";
		switch(type){
			case "any":
				out = parseAny(input);
			break;
			case "all":
				out = parseAll(input);
			break;
			case "none":
				out = parseNone(input);
			break;
		}
		return out;
	}
	
	/**
	 * Parses queries that match term A OR term B...
	 * @param s String the query to be parsed
	 * @return String the parsed string
	 */
	public static String parseAny(String s){
		s = clean(s);
		s = s.replaceAll("[ ]+", "\\|");
		s = s.replaceAll("[|]+", "|");
		return s;
	}
	
	/**
	 * Parses queries that match term A AND term B
	 * @param s String the query to be parsed
	 * @return String the parsed string
	 */
	public static String parseAll(String s){
		s = clean(s);
		return s;
	}	
	
	/**
	 * Parses queries that don't match term A AND term B
	 * @param s String the query to be parsed
	 * @return String the parsed string
	 */
	public static String parseNone(String s){
		String out = "";
		s = clean(s);
		String[] terms = s.split(" ");
		for(String term : terms){
			term = term.replaceAll("[^A-Za-z0-9\\|\\* ]", "");
			if(term.matches("[^a-zA-Z0-9]")) out += term +" ";
			else out += "-"+term + " ";
		}
		return out;
	}
	
	/**
	 *Sanitizes input and remove characters that aren't valid for a term
	 *Alphanumeric are okay, | denote OR, - denote not, and * denotes a wildcard
	 * @param s String the query to be cleaned
	 * @return String the cleaned string
	 */
	public static String clean(String s){
		s = s.replaceAll("[^A-Za-z0-9\\|\\-\\* ]", " ");
		s = s.replaceAll("[\\s]+", " ");
		s = s.trim();
		return s;
	}
}