package edu.ncrn.cornell.ced2ar.eapi.constructors;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;
import edu.ncrn.cornell.ced2ar.sql.dao.VariableDAO;
import edu.ncrn.cornell.ced2ar.sql.dao.VariableNoteDAO;
import edu.ncrn.cornell.ced2ar.sql.dao.VariableSumStatDAO;
import edu.ncrn.cornell.ced2ar.sql.dao.VariableValueDAO;
import edu.ncrn.cornell.ced2ar.sql.models.Variable;
import edu.ncrn.cornell.ced2ar.sql.models.VariableNote;
import edu.ncrn.cornell.ced2ar.sql.models.VariableSumStat;
import edu.ncrn.cornell.ced2ar.sql.models.VariableValue;

/**
 *This class builds a DDI Codebook XML from scratch
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Codebook {
	
	private static final Logger logger = Logger.getLogger(Codebook.class);
	private ClassPathXmlApplicationContext CONTEXT = null;
	private String HANDLE;	
	private XMLHandle XH;
	
	public static void main(String[] args){	
		Codebook codebook = new Codebook("cnss2013");
		codebook.init();
		System.out.println(codebook.getDDI());
    }
	
	/**
	 * Builds a codebook
	 * @param h - Handle of codebook or codebook id
	 */
	public Codebook(String h){
		HANDLE = h;
		
		//TODO: replace with actual title if possible
		String initialDDI = "<?xml version='1.0' encoding='UTF-8'?>"
		+"<codeBook>"
		+"<docDscr/><stdyDscr><citation><titlStmt><titl>"+HANDLE+"</titl></titlStmt></citation></stdyDscr>"
		+"<dataDscr/>"
		+ "</codeBook>";
		XH = new XMLHandle(initialDDI,Config.getInstance().getSchemaURI());
 	}
	
	private void init(){
		try{
			CONTEXT = new ClassPathXmlApplicationContext("ced2ar-db-beans.xml");
			constructVars();
				
		}finally{
			CONTEXT.close(); 
		}	
	}
	
	private void constructVars(){
		long startTime = System.currentTimeMillis();
		
		System.out.println("Codebook generation has started");
		logger.debug("Codebook generation has started");

		VariableDAO  variableDAO = (VariableDAO) CONTEXT.getBean("variableDAO");
		ArrayList<Variable> vars = variableDAO.getVarsInCodebook(HANDLE);
		
		System.out.println("Adding vars...");
		logger.debug("Adding vars...");
		
		for(Variable var : vars){
			String varName = var.getName();
			String label = var.getLabel();
			int startPos = var.getStartPos();
			int endPos = var.getEndPos();
			
			//TODO: How to add original name
			String originalName = var.getOriginalName();
			if(originalName != null && !originalName.equals("")){
				String xpathOName = "/codeBook/dataDscr/var[@name='"+varName+"']/codInstr";
				String oNameMsg = "This variable's name was change from "+originalName;
				XH.addReplace(xpathOName, oNameMsg, true, true, false, true);
			}
			
			String xpath = "/codeBook/dataDscr/var[@name='"+varName+"']/labl";
			XH.addReplace(xpath, label, true, true, false, true);
			
			//TODO: Better error handling for XML handle
			if(XH.getError() != null){
				String error = XH.getError();
				logger.error("XML constructor error: "+ error + "\n for: "+xpath);
				System.out.println("XML constructor error: "+ error + "\n for: "+xpath);
				break;
			}
			
			if(startPos != 0 && endPos != 0){		
				//TODO: Might not work because of the constraints of XML handle
				int width = endPos - startPos;
				String xpathWidth = "/codeBook/dataDscr/var[@name='"+varName+"']/location"
				+"[@width='"+width+"']";
				String xpathStart = "/codeBook/dataDscr/var[@name='"+varName+"']/location"
				+"[@StartPos='"+width+"']";
				String xpathEnd = "/codeBook/dataDscr/var[@name='"+varName+"']/location"
				+"[@EndPos='"+width+"']";
				XH.addReplace(xpathWidth, label, true, true, false, true);
				XH.addReplace(xpathStart, label, true, true, false, true);
				XH.addReplace(xpathEnd, label, true, true, false, true);		
			}
			
			System.out.println("Adding values...");
			logger.debug("Adding values");
			
			//Parse values this way to skip long numeric values
			VariableValueDAO valueDAO = (VariableValueDAO) CONTEXT.getBean("variableValueDAO");
			ArrayList<VariableValue> values = valueDAO.getValuesForVar(varName, HANDLE);
			
			
			String type = "character";
			if(values.size() > 0){
				try{  
				    Double.parseDouble(values.get(0).getValueLabel());  
				    type = "numeric";
				}catch(NumberFormatException nfe){}  
	
				if(values.size() < 100 || !type.equals("numeric")){
					for(VariableValue varValue : values){
						//Values and labels aren't trimed in SQL...
						String value = varValue.getValue().trim();
						String valLabel = varValue.getValueLabel().trim();
				
						//System.out.println("Adding "+value + " - " + label + " to " + varName);
						logger.debug("Adding "+value + " - " + label + " to " + varName);
						
						String xpathCat = "/codeBook/dataDscr/var[@name='"+varName+"']/catgry";
						String catValues = "";
						XH.addReplace(xpathCat, catValues, true, true, true, true);
						
						String xpathValue = "/codeBook/dataDscr/var[@name='"+varName+"']/catgry[last()]/catValu";
						XH.addReplace(xpathValue, value, true, true, false, true);
						
						String xpathLabel = "/codeBook/dataDscr/var[@name='"+varName+"']/catgry[last()]/labl";
						XH.addReplace(xpathLabel, valLabel, true, true, false, true);
					}
				}
			}

			String xpathType = "/codeBook/dataDscr/var[@name='"+varName+"']/varFormat/@type";
			XH.addReplace(xpathType, type, true, true, false, true);	

		}
		
		System.out.println("Adding sum stats...");
		logger.debug("Adding sum stats...");
		
		//Adds sum stats
		VariableSumStatDAO  sumStatDAO = (VariableSumStatDAO) CONTEXT.getBean("variableSumStatDAO");
		ArrayList<VariableSumStat> sumStats = sumStatDAO.getStatsForCodebook(HANDLE);
		for(VariableSumStat stat : sumStats){
			String varName = stat.getName();
			String type = stat.getStat();
			String value = Double.toString(stat.getStatValue());
			switch(type){
				case "median":
					type = "medn";
				break;
				case "std":
					type = "stdev";
				break;
				case "max":
					String xpathMax = "/codeBook/dataDscr/var[@name='"+varName+"']"
					+ "/valrng[1]/range[1]/@max";//
					XH.addReplace(xpathMax, value, true, true, false, false);
				break;
				case "min":
					String xpathMin = "/codeBook/dataDscr/var[@name='"+varName+"']"
					+ "/valrng[1]/range[1]/@min";
					XH.addReplace(xpathMin, value, true, true, false, false);
				break;
				default:
				break;
			}
			
			String xpath = "/codeBook/dataDscr/var[@name='"+varName+"']/sumStat[@type='"+type+"']";
			XH.addReplace(xpath, value, true, true, false, true);
			
		}
		
		//Adds notes to codebook
		//VariableNoteDAO  noteDAO = (VariableNoteDAO) CONTEXT.getBean("variableNoteDAO");
		//ArrayList<VariableNote> notes = noteDAO.getNotesForCodebook(HANDLE);
		//TODO: Add notes

		//Add namespace information
		XH.addNamespace();
		
		long endTime = System.currentTimeMillis();
		logger.debug("Done. Process took "+((endTime - startTime) / 1000.0) + " seconds");
		System.out.println("Done. Process took "+((endTime - startTime) / 1000.0) + " seconds \n");
		
	}
	
	public String getDDI(){
		return XH.docToString();
	}
}