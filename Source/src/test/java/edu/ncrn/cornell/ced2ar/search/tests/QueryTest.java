package edu.ncrn.cornell.ced2ar.search.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ncrn.cornell.ced2ar.web.classes.Query;

/**
 * Class to test the private methods of the Query.java class within the package edu.ncrn.cornell.ced2ar.web.classes
 * @author NCRN-Cornell (Kyle)
 */

public class QueryTest{

	/**
	 * Method allNull.
	 * @throws NullPointerException
	 */
	@Test(expected=NullPointerException.class)
	public void allNull() throws NullPointerException{
		Query q = new Query(null,null,null,null,null,null);
		q.getQuery();
	}
	
	@Test
	public void testClean(){
		Query q = new Query ("}{?test12!@#$%^&|++#$","","","","","");
		assertEquals("ALLFIELDS=test12|",q.getQuery());
	}
	
	@Test
	public void testSeperate(){
		Query q = new Query ("name=age","","","","","");
		assertEquals("ALLFIELDS=name,ALLFIELDS=age",q.getQuery());
		q = new Query ("test,n=age,l=date,f=descr,c=foo,t=bar","","","","","");
		assertEquals("ALLFIELDS=test,variablename=age,variablelabel=date,variabletext=descr,variablecodeinstructions=foo,variableconcept=bar",q.getQuery());
	}
	
	@Test
	public void testBuild(){
		Query q = new Query ("n=age|date|race","","","","","");
		assertEquals("variablename=age|variablename=date|variablename=race",q.getQuery());
		q = new Query ("","age|date|race","","","","");
		assertEquals("variablename=age|variablename=date|variablename=race",q.getQuery());
		q = new Query ("age","date","race","start","end","test");
		assertEquals("ALLFIELDS=age,variablename=date,variablelabel=race,variabletext=start,variablecodeinstructions=end,variableconcept=test",q.getQuery());
		q = new Query ("age date race start end test","","","","","");
		assertEquals("ALLFIELDS=age,ALLFIELDS=date,ALLFIELDS=race,ALLFIELDS=start,ALLFIELDS=end,ALLFIELDS=test",q.getQuery());
		q = new Query ("ancestry -second","","","","","");
		assertEquals("ALLFIELDS=ancestry,ALLFIELDS!=second",q.getQuery());
		q = new Query ("ancestry l=-second","","","","","");
		assertEquals("ALLFIELDS=ancestry,variablelabel!=second",q.getQuery());
		q = new Query ("ancestry","-second","","","","");
		assertEquals("ALLFIELDS=ancestry,variablename!=second",q.getQuery());
		q = new Query ("age** n==date","","","","","");
		assertEquals("ALLFIELDS=age*,variablename=date",q.getQuery());
		q = new Query ("age age|age","","","","","");
		//assertEquals("ALLFIELDS=age",q.getQuery());
		q = new Query ("age||date","","","","","");
		assertEquals("ALLFIELDS=age|ALLFIELDS=date",q.getQuery());
		q = new Query ("age,date","","","","","");
		assertEquals("ALLFIELDS=age,ALLFIELDS=date",q.getQuery());
		q = new Query ("age, date","","","","","");
		assertEquals("ALLFIELDS=age,ALLFIELDS=date",q.getQuery());
		q = new Query ("namen=aget=date","","","","","");
		assertEquals("ALLFIELDS=namen,ALLFIELDS=aget,ALLFIELDS=date",q.getQuery());
		q = new Query ("n      =age ||| t    = age","","","","","");
		assertEquals("variablename=age|variableconcept=age",q.getQuery());
		
	}
}