package edu.ncrn.cornell.ced2ar.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**Manages the parsing and most validation of API params
 *and builds xquery for searching 
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class SearchConstructor {
	//Reference and validation constants
	private ArrayList<String> WHERE_FIELDS = new ArrayList<String>();
	private ArrayList<String> CODEBOOK_SORT_FIELDS = new ArrayList<String>();
	private ArrayList<String> DOCDSCR_SORT_FIELDS = new ArrayList<String>();
	private HashMap<String, String> API_DDI_PATH = new HashMap<String, String>();
	private HashMap<String, String> VARNAME_MAPPING = new HashMap<String, String>();
	private String ERROR = null;

	//Query construction constants
	//RETURN_FIELD can be 'variables' or 'codebooks'
	//Codebooks isn't very useful to search with current options
	private String RETURN_FIELD = null;
	private String WHERE = null;
	private String SORT = null;
	private String LIMIT = null;
	private String TYPE = null;	
	
	//Field weight constants
	private String NAME_WEIGHT = "25";
	private String LABEL_WEIGHT = "20";
	private String TEXT_WEIGHT = "10";
	private String CONCEPT_WEIGHT = "5";
	private String CODE_INSTR_WEIGHT = "3";
	
	//private boolean PARTIAL = false; //No longer supporting full search results, all results are partial
	private boolean QUERY_EXP = false;
	private boolean RANKING = false;
	private String COUNT_CODE = "";
	
	//constants for keeping track of scoring;
	private String LET_SCORES = "";
	private int VAR_COUNTER = 0;
	private ArrayList<String> SCORE_VARS = new ArrayList<String>();

	/**
	 * Constructor for SearchConstructor.
	 * @param returnParam String - What type results to search, either variables or codebooks
	 * @param whereParam String - Conditional statements as what specific fields should or shouldn't equal
	 * @param sortParam String - What field to sort by
	 * @param limitParam String - What range of results to return ie 1-10 or 11-20
	 * @param type String - Format of results (XML, JSON, CSV)
	 * @param partial boolean - Whether or not to return full markup, or snippets of the markup
	 * @param exp boolean - Whether or not the search should use query expansion
	 */
	public SearchConstructor(String returnParam, String whereParam, String sortParam, 
	String limitParam, String type, boolean partial, boolean exp){
		initialize();
		//Args can be null, trying to trim null strings throws exception
		if(returnParam != null)
			this.RETURN_FIELD = returnParam.trim();
		if(whereParam != null)
			this.WHERE = whereParam.trim();
		if(sortParam != null)
			this.SORT = sortParam.trim();
		if(limitParam != null)
			this.LIMIT = limitParam.trim();
		if(type != null)
			this.TYPE = type.trim();
		//this.PARTIAL = partial;
		this.QUERY_EXP = exp;
	}
	
	/**
	 *Set constants for reference and validation of API syntax
	 */
	private void initialize() {
		//DDI mappings
		API_DDI_PATH.put("variables", "$var");
		API_DDI_PATH.put("codebooks", "$var/../..");
		API_DDI_PATH.put("variablename", "$var/@name");
		API_DDI_PATH.put("variablelabel", "$var/labl");
		API_DDI_PATH.put("variabletext", "$var/txt");
		API_DDI_PATH.put("variablecodeinstructions", "$var/codInstr");
		API_DDI_PATH.put("variableconcept", "$var/concept");
		API_DDI_PATH.put("codebooktitle","$var/../../docDscr/citation/titlStmt/titl");
		API_DDI_PATH.put("productdate","$var/../../docDscr/citation/prodStmt/prodDate/@date");
		API_DDI_PATH.put("id","$var/@ID");

		//Acceptable fields for WHERE condition
		WHERE_FIELDS.add("variablename");
		WHERE_FIELDS.add("variablelabel");
		WHERE_FIELDS.add("variabletext");
		WHERE_FIELDS.add("variablecodeinstructions");
		WHERE_FIELDS.add("variableconcept");
		WHERE_FIELDS.add("id");
		WHERE_FIELDS.add("codebooktitle");
		WHERE_FIELDS.add("filename");
		WHERE_FIELDS.add("productdate");
		WHERE_FIELDS.add("allfields");
		
		VARNAME_MAPPING.put("variablename", "$n");
		VARNAME_MAPPING.put("variablelabel", "$l");
		VARNAME_MAPPING.put("variabletext", "$t");
		VARNAME_MAPPING.put("variablecodeinstructions", "$i");
		VARNAME_MAPPING.put("variableconcept", "$c");

		//Acceptable sort field for codebooks
		CODEBOOK_SORT_FIELDS.add("codebooktitle");

		//Acceptable sort fields for document descriptions
		DOCDSCR_SORT_FIELDS.add("codebooktitle");
		DOCDSCR_SORT_FIELDS.add("productdate");
	}
	
//Get Functions
	/**
	 * Method getReturnField.
	 * @return String */
	public String getReturnField() {
		return RETURN_FIELD;
	}

	/**
	 * Method getWhere.
	 * @return String
	 */
	public String getWhere() {
		return WHERE;
	}

	/**
	 * Method getSort.
	 * @return String */
	public String getSort() {
		return SORT;
	}

	/**
	 * Method setSort.
	 * @param sort String
	 */
	public void setSort(String sort) {
		SORT = sort;
	}

	/**
	* Method getLimit.
	* @return String */
	public String getLimit() {	
		return LIMIT;
	}
	
	/**
	 * Method getError.
	 * @return String */
	public String getError(){
		return ERROR;
	}
	
	/**
	 * Method getCount.
	 * Returns number of possible results without limit parameter
	 * @return String */
	public String getCount(){	
		if(RETURN_FIELD.equals("variables") && COUNT_CODE.equals(""))
			return "let $ced2ar := collection('CED2AR') return count($ced2ar/codeBook/dataDscr/var)";
			//Rare edge case if user has no where statement
		return COUNT_CODE;
	}
	
	/**
	 * Method valid.
	 * Tests if all search clauses have a validate syntax
	 * @return boolean */
	public boolean valid() {
		validateWhere();
		validateSort();
		validateLimit();
		return ERROR == null ? true : false;
	}
	

	/**
	 *Validates where parameter
	 */
	private void validateWhere() {
		if (WHERE != null && WHERE.length() > 0) {
			String[] termArray = WHERE.split("[,\\|]");

			// loop through each term and split it by the = sign
			for (int i = 0; i < termArray.length; i++) {
				String expression = termArray[i];
				if (expression.contains("><")) {
					ERROR += "'><' not a valid operator.";
					break;
				} else {
					// replace == with = and replace <> with !=
					String testExp = expression.replace("==", "=").replace(
							"<>", "!=");
					// every where term must consist of two terms separated by a
					// (=, !=, <, >)
					String[] fieldAndValue = testExp
							.split("=(?!=)|(\\!=)|<(?!>)|>(?!<)");

					if (fieldAndValue.length != 2) {
						ERROR += "Invalid number of operators found: "
								+ testExp;
						break;
					} else {
						String field = fieldAndValue[0];
						String value = fieldAndValue[1];
						if (field != null && field.length() > 0) {
							if (!WHERE_FIELDS.contains(field.toLowerCase())) {
								ERROR += " \""
										+ fieldAndValue[0]
										+ "\" is not a valid search field. The list of search fields is: "
										+ StringUtils.join(this.WHERE_FIELDS, ", ");
							}
						} else {
							ERROR += "Error: no field supplied.";
						}

						if (value != null && value.length() > 0) {
							// check to make sure wildcards are only at the
							// beginning and/or end
							if (value.indexOf("*") >= 0) {
								if (value.length() > 1) {
									if(value.indexOf("*",1) >= 0 && value.indexOf("*",1) != value.length()-1){
										ERROR += "Wildcard (*) is only allowed at the ends of a value";
									}
								}
							}
						} else {
							ERROR += "Error: no value sup	plied.";
						}
					}
				}
			}
		}
	}
	
	/**
	 *Validates Sort 
	 */
	private void validateSort() {
		if (SORT != null && SORT.length() > 0) {
			String[] sortTerms = SORT.split(",");

			for (int i = 0; i < sortTerms.length; i++) {
				String sortTerm = sortTerms[i].toLowerCase().replace("+", "").replace(" ", "").replace("-", "");

				if (!WHERE_FIELDS.contains(sortTerm)|| sortTerm.equals("allfields")) {
					ERROR += "\""
					+ sortTerm
					+ "\" is an invalid sort term. Valid sort terms include: "
					+ StringUtils.join(WHERE_FIELDS, " or ").replace(
							"or allfields", "");
				}

				if (RETURN_FIELD.equals("codebooks") && !CODEBOOK_SORT_FIELDS.contains(sortTerm)) {
					ERROR += "\""
					+ sortTerm
					+ "\" is an invalid sort term. When searching codebooks, the following sort terms are valid: "
					+ StringUtils.join(CODEBOOK_SORT_FIELDS, " or ");
				}
			}
		}
	}
	
	/**
	 *Validates limit parameter 
	 */
	private void validateLimit() {
		if (LIMIT != null && LIMIT.length() > 0) {
			//if a single int and has no range ie limit=10
			if(LIMIT.matches("[0-9]+")){
				//Format into #-# syntax, ie limit=1-10
				LIMIT = "1-"+LIMIT;
			}else{
				//split limit by '-', already formatted limit=1-10
				String[] limitRange = LIMIT.split("-");
				for(int i = 0; i < limitRange.length; i++) limitRange[i] = limitRange[i].trim(); 
	
				if (limitRange.length != 2) {
					ERROR += "Too many ranges. Limit must be a range between two numbers, ie 1-20.";
					return;
				}
				//must only contain numbers
				try{
					int start = Integer.parseInt(limitRange[0]);
					int stop = Integer.parseInt(limitRange[1]);
					if (start > stop){
						ERROR += "Starting limit range must be less than or equal to stopping range";
					}
					else{
						/*xquery return $var , 3, 5) will not return results 3-5. It will start at the offset of 3, and return 5 results
						 *to keep the syntax limit=3-5, that statement needs to be parsed into return $var , 3, 3)*/
						if(start != 1){
							stop = (stop+1)-start;
							LIMIT = Integer.toString(start) + "-" + Integer.toString(stop);
						}
					}
				}
				catch(NumberFormatException e){
					ERROR += "Limit range must contain only numeric characters";
				}
			}
		}
	}

//Query Construction Functions
	
	/**
	 * Method buildXquery
	 * Main function to construct xquery to get variables, codebooks and document descriptions
	 * @return String */
	public String buildXquery(){
		String statement = "let  $ced2ar := collection('CED2AR') ";
		String limit = buildLimitRange();
		String sort = buildSortStatement();
		String where = "";
		String whereStatement = buildWhereStatement();
		where = (whereStatement.equals("") ? "" : " where " + whereStatement) + " ";
		if(SCORE_VARS.size() > 0){
			String calc = "";
			for(String v : SCORE_VARS){
				v = v.trim();
				if(v.startsWith("$n")) calc += " ("+NAME_WEIGHT+"*"+v+") +";
				else if(v.startsWith("$l")) calc += " ("+LABEL_WEIGHT+"*"+v+") +";
				else if(v.startsWith("$t")) calc += " ("+TEXT_WEIGHT+"*"+v+") +";
				else if(v.startsWith("$i")) calc += " ("+CODE_INSTR_WEIGHT+"*"+v+") +";
				else if(v.startsWith("$c")) calc += " ("+CONCEPT_WEIGHT+"*"+v+") +";
			}
			calc = calc.substring(0, calc.length() - 1);
			LET_SCORES += "let $score := "+calc;
			sort = "order by $score descending, lower-case($var/@name)";
		}
		switch (RETURN_FIELD.toLowerCase()) {
			case "variables":
					//No longer supporting search results with full docDscr. Too slow, too error prone
					String returnShort = "return <var codebook=\"{$titl}\" handle=\"{$handle}\">"
					+ "{$var/@name}{$var/@ID}{$var/child::*}</var> ";
					
					//CSV
					if(TYPE.toLowerCase().equals("csv")){
						returnShort = 
						" return string-join((data($var/@name),',',"
						+ "replace($var/labl/text(),',',''),',',"
						+ "$titl/text(),',',$version,';&#xa;'),'') ";
					}
					//Else return short XML
					statement+=
					" return "+
					(!limit.equals("") ? "subsequence( " : "") +		
					" for $codebook in $ced2ar "+
					" let $handle :=  replace(data(document-uri($codebook)),'CED2AR/','')"+
					" for $var in $codebook/codeBook/dataDscr/var"+
					" let $titl := $var/../../docDscr/citation/titlStmt/titl"+
					" let $version := for $cI in collection('index')/codeBooks/codeBook" +
					" for $cV in $cI/version"+
					" where $handle =  string-join(($cI/@handle,$cV/@v),'')"+
					" return string-join(($cI/@handle,$cV/@v),',') "+ 
					LET_SCORES + " " +
					where + " " + 
					sort + " " +  
					returnShort +
					limit + " ";		
		break;
		case "codebooks":
			statement += "for $codebook in $ced2ar/codeBook " +
					"let $var := $codebook/dataDscr/var " +
					where+" "+sort+" "+
					" return $var/../../docDscr/citation/titlStmt/titl";
		break;
		}	
		return statement;
	}
	
	/**
	 * Method buildLimitRange.
	 * Constructs limit appropriate for xquer	
	 * @return String the limit statement*/
	private String buildLimitRange() {
		String rangeStmt = "";
		if (LIMIT != null && LIMIT.length() > 0) {
			String[] ranges = LIMIT.split("-");
			if (ranges != null && ranges.length == 2) {
				rangeStmt = ", " + ranges[0].toString() + ", "
						+ ranges[1].toString() + ")";
			}
		}
		
		return rangeStmt;
	}
	
	/**
	 * Method buildSortStatement.
	 * Constructs sorting statement  for xquery	
	 * @return String the sort statement*/
	private String buildSortStatement() {
		String sortStmt = "";
		if (SORT != null && SORT.length() > 0) {
			String[] sortTerms = SORT.split(",");

			for (int i = 0; i < sortTerms.length; i++) {
				String direction = "";
				String sortTerm = sortTerms[i];
				//Need to add an order by outside the main loop for the xquery statement if sorting by codebook
				if (sortTerm.indexOf("-") >= 0) {
					direction = "descending";
				}
				sortTerm = sortTerm.toLowerCase().replaceAll("[\\+\\- ]", "");
				String concat = sortStmt.length() > 0 ? ", " : "";
				sortStmt += concat + " lower-case(" + API_DDI_PATH.get(sortTerm) + ") " + direction;
			}
			//Sorting is case sensitive and desired should not be case sensitive
			String out = sortStmt.length() > 0 ? " order by " + sortStmt +" " : "";
			return out;
		} else //Should default to var name
			if(RETURN_FIELD.toLowerCase().equals("variables")){
				this.RANKING = true;
				return "order by lower-case($var/@name)";
			}else{
				return "";
			}
	}
	
	/**
	 * Method buildWhereStatement.
	 * @return String the where statement*/
	private String buildWhereStatement() {
		StringBuilder whereBuilder = new StringBuilder();	
		// parse where
		if (WHERE != null && WHERE.length() > 0) {
			// split the string by , or |, indicating 'and' and 'or' expressions
			String[] termArray = WHERE.split("[,\\|]");
			
			//need to append parentheses on either end of "or" block
			int isOrCount = 0;
			int orIndex = 0;//Index of where to insert opening parentheses
			// loop through each term and split it by the = sign
			for (int i = 0; i < termArray.length; i++) {
				
				String expression = termArray[i];
				String bOperator = "";

				// BOOLEAN OPERATOR
				if (i > 0) {
					bOperator = WHERE.substring(WHERE.indexOf(expression) - 1,
							WHERE.indexOf(expression));
					// logger.info(bOperator);
				}

				// COMPARE OPERATOR
				// replace == with = and replace <> with !=
				String refExp = expression.replace("==", "=").replace("<>","!=");

				// every where term must consist of two terms separated by a (=,
				// !=, <, >)
				String[] fieldAndValue = refExp.split("=(?!=)|(\\!=)|<(?!>)|>(?!<)");

				// build where statement
				String field = fieldAndValue[0].toString();
				String value = fieldAndValue[1].toString();

				// logger.info(String.format("field: %s | value: %s", field,
				// value));

				// get the operator (=,!=,<,>)
				String operator = refExp.replace(field, "").replace(value, "");
				
				// logger.info(operator);
				
				if (field != null && field.length() > 0 && value != null && value.length() > 0) {
					ArrayList<String> whereSubs = new ArrayList<String>();
					String whereSubStmt;
					String newStmt = "";
					String concat = i > 0 ? bOperator.replace(",", "and").replace("|", "or") : "";
					concat=concat.trim();
					//If not first where statement
					if(i > 0){
						//If operator has not statement
						if(operator.equals("!=")){
							//If bOperator has or, join with or not
							if(bOperator.equals("|")){
								concat = "or not";
							//Else bOperator is and, join with and not
							}else{
								concat = "and not";
							}
						}
					}
					
					//Should insert "not" if first statement is a negative, rather than and not
					if(i == 0 && operator.equals("!=")){
						concat = "not";
					}
					
					if (field.toLowerCase().equals("allfields")) {
						for (String f : WHERE_FIELDS) {							
							if (!f.equals("allfields") && !f.equals("filename")  
								&& !f.equals("codebooktitle") && !f.equals("id") && !f.equals("productdate")) {
								whereSubStmt = buildWhereSubStatement(f, value, operator);
								whereSubs.add(whereSubStmt);
							}
						}						
						//Added leading parenthesis here between concat and allfield statement, incremented isOrCount
						newStmt = concat + " (("+ StringUtils.join(whereSubs, " or ") + ") ";	
						isOrCount++;
					} else {
						whereSubStmt = buildWhereSubStatement(field, value, operator);
						newStmt = concat + " (" + whereSubStmt + ") ";	
					}
					
					if (!concat.equals("or") && isOrCount > 0 && i > 1){

						//insert closing )
						whereBuilder.append(") " + newStmt);
						isOrCount--;
					}else if(!concat.equals("or") &&  whereBuilder.length() > 0){	
						whereBuilder.append(newStmt);
						orIndex += whereBuilder.length() + concat.length() + 1;

					}else{
						whereBuilder.append(newStmt);
					}
					
					if(concat.equals("or")){	
						//insert opening
						whereBuilder.insert(orIndex,"(");
						
						isOrCount++;
					}
				}
			}
			//Adding closing ) if or block not closed
			while(isOrCount > 0){	
				whereBuilder.append(")");
				isOrCount--;
			}
		}
		if (whereBuilder != null && whereBuilder.length() > 0) {
			COUNT_CODE = fetchCount(whereBuilder.toString());
			return whereBuilder.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * Method buildWhereSubStatement.
	 * *Utilized by buildWhere Statement
	 * @param field String the field we will specify; i.e. txt, labl, etc
	 * @param value String the value of the field; i.e. "age"
	 * @param operator String 
	 * @return String the where sub statement*/
	private String buildWhereSubStatement(String field, String value, String operator) {	
		String expression = "";
		String matchOption = "";
		String comparision = " contains text ";
		value = value.replace("\"", "'");//Doubled quotes are illegal
		
		if (value.indexOf("*") < 0) {
			// if there is no wildcard character, use stemming
			matchOption = "using stemming";
		} else {
			if(value.charAt(0) == '*')
				value= "." + value;
			if(value.endsWith("*"))
				value= value.substring(0,value.length()-1)+".*";
			matchOption = "using wildcards";
		}
				
		if(field.equals("filename")){
			return " (replace(document-uri($codebook),'CED2AR/','') eq '"+value+"') ";
		}else if(field.equals("codebooktitle") || field.equals("ID" )){
			comparision = " = ";
			matchOption = "";
		}
		if(QUERY_EXP){
			//TODO: Modify matchOption to include thesauri ie:
			//thesaurus at "URI" relationship "UF" 
		}
		
		String rawValue = value.replaceAll("[\\*\\.]", "").toLowerCase();
		if(rawValue.length() != 1){
			expression +="("+API_DDI_PATH.get(field)+ comparision + "  (\""+value+"\" "+matchOption+"))";
		}else{ 
			expression +=" starts-with(lower-case("+API_DDI_PATH.get(field)+"),\""+rawValue+"\")";
		}
		
		if(RANKING && rawValue.length() > 1){
			String var = VARNAME_MAPPING.get(field)+VAR_COUNTER;
			this.LET_SCORES += " let score "+var+" :="+expression;
			VAR_COUNTER++;
			SCORE_VARS.add(var);
			return "("+var+" > 0)";			
		}
		
		return expression;
	}
		
	/**
	 * Method fetchCount.
	 * Returns total number of results possible of count
	 * @param whereStmt String the where statement that will return the results
	 * @return String the number of elements retrieved by executing the statement*/
	private String fetchCount(String whereStmt){
		String count = "";
		switch (RETURN_FIELD.toLowerCase()) {
		case "variables":
			count += "sum(for $ced2ar in collection('CED2AR')"
					+ " return count( "
					+ " for $codebook in $ced2ar"
					+ " for $var in $codebook/codeBook/dataDscr/var" 
					+ LET_SCORES
					+ (whereStmt.equals("") ? "" : " where " + whereStmt) 
					+ " return $var ))";
			break;
		}
		return count;
	}
}