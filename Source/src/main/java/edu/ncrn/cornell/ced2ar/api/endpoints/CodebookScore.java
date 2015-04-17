package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/** 
 * For the {baseUrl}/codebooks/{handle}/score endpoint
 * Retrieves a completeness score to show the quality of a book
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class CodebookScore extends ServerResource {
	
	@Autowired
	private ServletContext context;
	
	final int LABEL_MIN_LENGTH = 0;
	final int TXT_MIN_LENGTH = 100;
	
	final double LABEL_WEIGHT = 4.0;
	final double TXT_WEIGHT = 3.5;
	final double CATGRY_WEIGHT = 1.5;
	final double SUM_STAT_WEIGHT = 1.0;
	final double PERFECT_SCORE = LABEL_WEIGHT + TXT_WEIGHT + CATGRY_WEIGHT + SUM_STAT_WEIGHT;
			
	/**
	 * Retrieving scoring information on a codebook
	 * @param variant Variant specifies what media type to retrieve data in
	 * @return the representation of the data in either XML or Json
	 */
	@Get("json")
	public Representation represent(Variant variant) {
		String handle = (String) getRequestAttributes().get("codebookId");
		if (handle == null || handle.length() == 0) {
			String message = " \"" + handle + "\" is an invalid handle";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		if(!Utilities.codebookExists(handle)){
			String message = " \"" + handle + "\" does not exist";
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,message);	
		}

		String xquery = "for $v in collection('CED2AR/"+handle+"')/codeBook/dataDscr/var "	
		+" order by $v/@name return string-join((data($v/@name),"
	    + "string(string-length($v/labl)>"+LABEL_MIN_LENGTH+"),"
	    + "string(string-length($v/txt)>"+TXT_MIN_LENGTH+"),"
	    + "string(exists($v/catgry)),"
	    + "string(exists($v/sumStat))"
	    + "),',')";

		String resultB = BaseX.getXML(xquery,false).trim();

		double lablScore = 0.0;
		double txtScore = 0.0;
		double catScore = 0.0;
		double sumStatScore = 0.0;
		double varTotalScore = 0.0;
		
		String varJSON = "\"variables\":{";
		String varLabelJSON = "\"labl\":[";
		String varTxtJSON = "\"txt\":[";
		String varCatJSON = "\"cat\":[";
		String varsumStatJSON = "\"sumStat\":[";
		String[] vars = resultB.split(" ");
		if(!resultB.equals("")){
			for(String var : vars){
				String[] fields = var.split(",");
				if(fields[1].equals("true")){ 
					lablScore++;
				}else{
					if(!varLabelJSON.endsWith("["))  varLabelJSON+=",";
					varLabelJSON+="\""+fields[0]+"\"";
				}
				if(fields[2].equals("true")){ 
					txtScore++;
				}else{
					if(!varTxtJSON.endsWith("["))  varTxtJSON+=",";
					varTxtJSON+="\""+fields[0]+"\"";
				}
				if(fields[3].equals("true")){ 
					catScore++;
				}else{
					if(!varCatJSON.endsWith("["))  varCatJSON+=",";
					varCatJSON+="\""+fields[0]+"\"";
				}
				if(fields[4].equals("true")){ 
					sumStatScore++;
				}else{
					if(!varsumStatJSON.endsWith("["))  varsumStatJSON+=",";
					varsumStatJSON+="\""+fields[0]+"\"";
				}
			}
		}
		varLabelJSON += "],";
		varTxtJSON += "],";
		varCatJSON += "],";
		varsumStatJSON += "],";
		
		lablScore = lablScore/vars.length;
		txtScore = txtScore/vars.length;
		catScore = catScore/vars.length;
		sumStatScore = sumStatScore/vars.length;
		varTotalScore = ((lablScore * LABEL_WEIGHT)+(txtScore * TXT_WEIGHT)
		+(catScore * CATGRY_WEIGHT)+(sumStatScore * SUM_STAT_WEIGHT))/PERFECT_SCORE;
	
		varJSON += varLabelJSON + varTxtJSON + varCatJSON + varsumStatJSON;
		varJSON += "\"scores\":{\"labl\":"
		+lablScore+",\"txt\":"
		+txtScore+",\"cat\":"
		+catScore+",\"sumStat\":"
		+sumStatScore+",\"overall\":"
		+varTotalScore+"}}";

		//See external doc for field listing
		ArrayList<String[]> titlePageFields = new ArrayList<String[]>();
		titlePageFields.add(new String[] {"docDscr/citation/prodStmt/prodDate",".7","Production Date"});
		titlePageFields.add(new String[] {"docDscr/citation/prodStmt/producer",".8","Document Producer"});
		titlePageFields.add(new String[] {"stdyDscr/citation/prodStmt/producer",".8","Study Producer"});
		titlePageFields.add(new String[] {"stdyDscr/citation/distStmt/distrbtr",".5","Distributor"});
		titlePageFields.add(new String[] {"docDscr/citation/biblCit",".8","Codebook Citation"});
		titlePageFields.add(new String[] {"stdyDscr/citation/biblCit",".8","Data Citation"});
		titlePageFields.add(new String[] {"stdyDscr/stdyInfo/abstract",".9","Abstract"});
		titlePageFields.add(new String[] {"fileDscr/@URI",".9","File Description URI"});
		titlePageFields.add(new String[] {"fileDscr/fileTxt/fileName",".9","File Description Name"});
		titlePageFields.add(new String[] {"fileDscr/fileTxt/fileType",".9","File Description Type"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[@ID]",".7","Data Access IDs"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[@ID]/useStmt/restrctn",".5","Data Access Restrictions"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/restrctn",".7","Access Restrictions (Default)"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/confDec",".5","Access Requirements"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/conditions",".5","Access Conditions"});	
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/specPerm",".5","Access Permission Requirements"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/citReq",".5","Citation Requirements"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/disclaimer",".5","Disclaimer"});
		titlePageFields.add(new String[] {"stdyDscr/dataAccs[1]/useStmt/contact",".6","Contact"});
		titlePageFields.add(new String[] {"stdyDscr/method/dataColl/collMode",".5","Methodology"});
		titlePageFields.add(new String[] {"stdyDscr/method/dataColl/sources/dataSrc",".5","Data Sources"});
		titlePageFields.add(new String[] {"stdyDscr/othrStdyMat/relMat",".6","Related Material"});
		titlePageFields.add(new String[] {"stdyDscr/othrStdyMat/relPubl",".6","Related Publications"});
		titlePageFields.add(new String[] {"stdyDscr/othrStdyMat/relStdy",".6","Related Studies"});
		titlePageFields.add(new String[] {"docDscr/docSrc/biblCit",".5","Bibliographic Citation"});
		
		String xquery2 = "let $codebook := collection('CED2AR/"+handle+"')/codeBook return string-join((";
		double perfectTitleScore = 0.0;
		for(String[] field : titlePageFields){
			if(perfectTitleScore != 0.0){
	        	xquery2+=",";
	    	}
			xquery2+="string(number(exists($codebook/"+field[0]+"))*"+field[1]+")";
		    perfectTitleScore += Double.parseDouble(field[1]);
		}
		
	    xquery2+="),',')";
	    
	    String results2 = BaseX.getXML(xquery2, false);
	    
	    String[] scores2 = results2.split(",");
	    double titleTotalScore = 0.0;

	    String titlePageJSON = "\"titlePage\":{";
		for(int i = 0;i < scores2.length;i++){
			String s  = scores2[i].trim();
			
			titlePageJSON+="\""+titlePageFields.get(i)[2]+"\":"+s+",";			
			titleTotalScore+=Double.parseDouble(s);
		}
		
		String titleScore  = Double.toString(titleTotalScore/perfectTitleScore);
		titlePageJSON += "\"Overall\":"+titleScore+"}";
		
		String overallScore =  "\"overallScore\":{\"total\":"+Double.toString(((varTotalScore)*0.6)
		+((titleTotalScore/perfectTitleScore)*0.4))+"}";

		Representation response = null;
		String jsonReturn = "{"+varJSON+","+titlePageJSON+","+ overallScore+"}";
		
		if(MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			response = new StringRepresentation(jsonReturn, MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return response;
		}else{
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
	}
}