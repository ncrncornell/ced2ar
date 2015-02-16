package edu.ncrn.cornell.ced2ar.web.classes;

import java.util.ArrayList;

/**
 *Builder handles the generic requests from searching and browsing,
 *and helps format form data into API syntax
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class Builder {
	private String limit;
	private String sort;
	private int pageSize;	
	
	/**
	 * This constructor is for when data is sorted
	 * @param pageNumber String page number of results current on
	 * @param show String results per page to show
	 * @param sortCol String column to sort by
	 * @param sortDir String direct to sort by (asc or desc)
	 */
	public Builder(String pageNumber,String show,String sortCol, String sortDir){
		this.pageSize = setPageSize(show);
		this.limit = setLimit(show,pageNumber);
		this.sort = setSort(sortCol, sortDir);
	}
	
	/**
	 * This constructor is for when data does not have specified sort terms
	 * @param pageNumber String page number of results current on
	 * @param show String results per page to show
	 */
	public Builder(String pageNumber,String show){
		this.pageSize = 1;
		this.limit = setLimit(show,pageNumber);
		this.sort = "";
	}
	
	/**
	 * Sets the number of results to display per page. Default is 10 variable per page
	 * Acceptable values are 1, 10, 25, 50, 100 or 250. Any other value will default to 10.
	 * * @param s String the possible page size to parse
	 * @return int the final page size
	 */
	private int setPageSize(String s){
		int pS = 10;
		//Sets page size if show parameter is set
		if(!s.equals("")){	
			try{
				pS = Integer.parseInt(s);
				if(pS != 1 & pS != 10 & pS != 25 & pS != 50 & pS != 100 & pS != 250)
					pS = 10;
			}catch(NumberFormatException  e){}
		}
		return pS;
	}
	
	/**
	 * Finds limit based off page size. API takes the arguments start index, and stop index ie &limit=1-10. 
	 * The first variables is indexed as 1, not 0
	 * 
	 * * @param show String 
	 * @param pn String the current page number
	 * @return String the number of variables to be displayed on the page
	 */
	private String setLimit(String show, String pn){
		int size = this.pageSize;
		int limitStart = 0;
		int limitEnd = 10;
		try{
			int pageNum = pn.equals("") ? 1 : Integer.parseInt(pn);
			limitEnd = pageNum * size;
			limitStart = limitEnd - (size-1);
		}catch(NumberFormatException  e){}
		String limit = Integer.toString(limitStart) + "-" +  Integer.toString(limitEnd);
		return limit;
	}
	
	/**
	 * Find column to sort by and direction
	 * variablename, variablelabel, or codebooktitle are valid
	 * Default is variablename
	 * * @param sC String the column to sort by
	 * @param sD String the direction to sort by
	 * @return String the formatted sort string
	 */
	private String setSort(String sC, String sD){
		String sort = "";
		if(!sC.equals("")){
			sort = "variablename";
			String dir = sD.equals("1") ? "-" : "+";
			switch(sC){
				case "1":
					sort = dir + "variablename";
				break;
				case "2":
					sort = dir + "variablelabel";
				break;
				case "3":
					sort = dir + "codebooktitle,+variablename";
				break;
			}
		}
		return sort;
	}
	
	/**
	 * Filters by codebooks based off session data
	 * * @param filters ArrayList<String[]> the list of current filters by codebook name
	 * @return String the formatted filter string
	 */
	public String getFilter(ArrayList<String[]> filters){
		String out = "";
		String filterArgs = "";
		for(String[] codebook: filters){
			if(codebook[5].equals("true")){
				if(filterArgs.length() > 0)
		    		filterArgs+= "|";
				filterArgs += "filename=" + codebook[0];
			}		
		}		
		if(filterArgs.length() > 0)
			out += filterArgs + ",";
		return out;
	}
	
	/**
	 *Removes paging information so controller 
	 *can return redirect information when filter is changed
	 * @param rQ String string containing the queries
	 * @param type String the last page
	 * @return String formatted request string
	 */
	public static String getRedirect(String rQ, String type){
		rQ = rQ.replaceAll("&d\\-1341904\\-p=\\d", "");
	 	rQ = rQ.replaceAll("&d\\-1341904\\-s=\\d", "");
	 	rQ = rQ.replaceAll("&d\\-1341904\\-p=\\d", "");
	 	rQ = rQ.replaceAll("\\++", "\\+");
		return rQ.equals("") ? "search" : type+"?"+rQ;
	}
	
	/**
	 *Returns current page size
	 * @return int page size
	 */
	public int getPageSize(){
		return this.pageSize;
	}
	
	/**
	 *Returns current limit arguments
	 * @return String limit arguments
	 */
	public String getLimit(){
		return this.limit;
	}

	/**
	 *Returns sort direction
	 * @return String sort direction
	 */
	public String getSort(){
		return this.sort;
	}
}