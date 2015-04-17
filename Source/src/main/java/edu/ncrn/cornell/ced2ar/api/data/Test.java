package edu.ncrn.cornell.ced2ar.api.data;

import java.util.ArrayList;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.ncrn.cornell.ced2ar.sql.dao.VariableNoteDAO;
import edu.ncrn.cornell.ced2ar.sql.models.VariableNote;

/**
 **For misc functions not to be included in the final build
 *@author NCRN Project Team
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Test {
	public static void main(String[] args)
	{
		ClassPathXmlApplicationContext context = null;
		try{
			context = new ClassPathXmlApplicationContext("ced2ar-db-beans.xml");
			VariableNoteDAO  v = (VariableNoteDAO) context.getBean("variableNoteDAO");
			ArrayList<VariableNote> notes = v.getNotesForVar("", "cnss2013");
	        for(VariableNote note : notes){
	        	System.out.println(
        			note.getCodebookID() + " " +
					note.getName() + " " +
					note.getID() + " " +
					note.getValue()
	        	);
	        }
		}finally{
			context.close(); 
		}
    }
}
