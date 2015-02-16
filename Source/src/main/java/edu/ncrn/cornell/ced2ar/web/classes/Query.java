package edu.ncrn.cornell.ced2ar.web.classes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *This class parses URLs generated from the web search and constructs API requests
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Query {
	private String ALLFIELDS;
	private String NAME;
	private String LABEL;
	private String DESCRIPTION;
	private String CONCEPT;
	private String TYPE;
	private String PARSED;
	private String ERROR;
	
	/**
	 * Constructor for Query.
	 * @param a String
	 * @param n String
	 * @param l String
	 * @param f String
	 * @param c String
	 * @param t String
	 */
	public Query(String a, String n, String l, String f, String c, String t){
		this.ALLFIELDS = a;
		this.NAME = n;
		this.LABEL = l;
		this.DESCRIPTION = f;
		this.CONCEPT = c;
		this.TYPE = t;
		this.PARSED = "";
		this.ERROR = "NONE";
		
		seperate();
		
		build(this.ALLFIELDS,"ALLFIELDS");
		build(this.NAME,"variablename");
		build(this.LABEL,"variablelabel");
		build(this.DESCRIPTION,"variabletext");
		build(this.CONCEPT,"variablecodeinstructions");
		build(this.TYPE,"variableconcept");
		
		//Or condition between two different fields causes this to be necessary
		//ie f=age|l=race TODO: find better solution. Low priority.
		PARSED = PARSED.replace("|,", "|");
	}
	
	/**
	 *Returns formed URL for API 
	 * @return String
	 */
	public String getQuery(){
		return this.PARSED;
	}
	
	/**
	 *Formats URI argument in proper API syntax
	 * @param s String the query input
	 * @param label String the query parameter (allfields, name, etc.)
	 */
	private void build(String s,String label){
		s = this.clean(s);
		String args = "";
		//Checks if query has alphanumeric characters
		if(s.replaceAll("[^A-Za-z0-9]", "").length() != 0){
			if(PARSED != null && !PARSED.equals("")){
				args+=",";
			}
			ArrayList<String> used = new ArrayList<String>();//Prevents adding duplicates

			String[] terms = s.split(" ");
			int i = 0;
			
			for(String term : terms){		
				//If pipe is encountered, inject pipe into arg to create or statement
				//Moving the declaration to here fixes the a|b c search bug
				//Previously, would not add , to ALLFIELDS=a|ALLFIELDS=b,ALLFIELDS=c because hasPipe was still true
				boolean hasPipe = false;
				if(term.equals("|")){
					    hasPipe = true;
						args+="|";
				}else if(term.replaceAll("[^A-Za-z0-9_]", "").length() != 0){
					term = term.replaceAll("\\*+","*");
					if(i != 0 && !hasPipe && !(PARSED.trim().endsWith("|"))){
						args +=",";
						hasPipe = false;
					}
					String prd = "=";
					//If term start with a - , this indicates a 'not' predicate
					if(term.matches("-[\\w* ]+")){
						prd = "!=";
						term = term.replaceAll("-", "");
					}
					//term should only contain alphanumeric characters or stars at this point
					term = term.replaceAll("[^A-Za-z0-9*_ ]", "");
					
					//If term not added yet
					if(!used.contains(term)){
						try {
							args+= label + prd + URLEncoder.encode(term,"UTF-8");
						} catch (UnsupportedEncodingException e) {}
						used.add(term);
					}else{
						args = args.substring(0,args.length()-1);//remove double pred
					}	
				}
				i++;
			}
			PARSED += args;	
		}
	}
	
	/**
	 *Removes bad characters and formats strings correctly
	 * @param s String the string to clean
	 * @return String the cleaned string
	 */
	private String clean(String s){
		//Eliminates everything but alphanumeric, pipes, hyphens, asterisks, underscores and spaces
		s = s.replaceAll(",", " ");
		s = s.trim().replaceAll("[^A-Za-z0-9\\|\\-\\*_ ]", "");
		s = s.replaceAll("[\\|]+", " \\| ");//Add spaces around pipes
		s = s.replaceAll("([\\*])\\1", "$1 $1");//Add spaces between double characters to deal with later
		return s;
	}
	
	/**
	 *Parses single field searches from ALLFIELDS in their respective field
	 *EX user inputs n=race in main search box, parses to search field varname for term "race"
	 *
	 */
	private void seperate(){
		this.ALLFIELDS = ALLFIELDS.replaceAll("[\\s]+", " ");
		this.ALLFIELDS = ALLFIELDS.replace(" =", "=");
		String allfieldsN = this.ALLFIELDS;
		Pattern p = Pattern.compile("[a-zA-Z]=");
	    Matcher m = p.matcher(ALLFIELDS);
	    ArrayList<Integer> ind = new ArrayList<Integer>();
	    while (m.find()) {
	    	ind.add(m.start());
        }
	    boolean hasEq = false;
	    if(ind.size() != 0){
	    		ind.add(ALLFIELDS.length());
	    		allfieldsN = allfieldsN.substring(0,ind.get(0));
	    		hasEq = true;
	    }
	    for(int i = 0; i < ind.size()-1; i++){
	    	//catches input like name=age. Before, query was split like this:
	    	//	ALLFIELDS=nam
	    	//	and trying to search for the variable e=age
	    	//Now, info splash is thrown up and query is translated to "name age"
	    	//Still more problems with the regex, though.
	    	if(i == 0 &&hasEq){
	    		if(ind.get(i) > 0){
	    			char beforePrefix = (ALLFIELDS.charAt(ind.get(i) - 1));
	    			if(!(beforePrefix == ',' || beforePrefix == '|' || beforePrefix == ' ')){
	    				allfieldsN = ALLFIELDS.replace("="," ");
	    				this.ERROR = "Improper search shortcut. See Documentation FAQ for syntax guidance.";
	    				break;
	    			}
	    		}
	    	}
	    	String subQuery = ALLFIELDS.substring(ind.get(i),ind.get(i+1));
	    	String prefix = subQuery.substring(0,1);
	    	switch(prefix){
	    		case "n":
	    			this.NAME += " "+ subQuery.substring(2).trim();
	    		break;
	    		case "l":
	    			this.LABEL += " "+ subQuery.substring(2).trim();
		    	break;
	    		case "f":
	    			this.DESCRIPTION += " "+ subQuery.substring(2).trim();
	    		break;
	    		case "c":
	    			this.CONCEPT += " "+ subQuery.substring(2).trim();
		    	break;
	    		case "t":
	    			this.TYPE += " "+ subQuery.substring(2).trim();
			    break;
			    default:
			    	//prefix is not valid, ignore
			    break;
	    	}
	    }	    
	  //Remove subquery from main query
	  this.ALLFIELDS = allfieldsN;
	}
	
	/**
	 * Method getError.
	 * @return String
	 */
	public String getError(){
		return this.ERROR;
	}
}