package edu.ncrn.cornell.ced2ar.search.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ncrn.cornell.ced2ar.api.SearchConstructor;

/**
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class SearchConstructorTest {
	
	@Test
	public void testValidateWhere(){
		SearchConstructor s = new SearchConstructor("","","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","this is invalid","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","ALLFIELDS=whatever|variablename!=something,variableconcept=test","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS><whatever","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","ALLFIELDS<>whatever==test","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","ALLFIELDS","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","ALLFIELDS=","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","=test","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","test=failed","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","ALLFIELDS=*asdf*","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=asdf*","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=*asdf","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=*","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=*a","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=a*","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=**","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","ALLFIELDS=*dfasdf*asdf*","","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","ALLFIELDS=sdf*a","","","",false,false);
		assertFalse(s.valid());
	}
	
	@Test
	public void testValidateSort(){
		SearchConstructor s = new SearchConstructor("","","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","","variable-name","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","","variable-name , variable+label","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","","variable-name , ALLFIELDS","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","","variables","","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","","codebooktitle","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("codebooks","","codebooktitle","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("codebooks","","variablename","","",false,false);
		assertFalse(s.valid());
	}
	
	@Test
	public void testValidateLimit(){
		SearchConstructor s = new SearchConstructor("","","","","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","","","a-a","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","",""," 11","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","","","121 ","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","",""," 14444 ","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","",""," 1 - 10 ","",false,false);
		assertTrue(s.valid());
		s = new SearchConstructor("","","","10-","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","","","10-10-10","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","","","10-10","",false,false);
		assertTrue(s.valid());
		assertEquals("10-1",s.getLimit());
		s = new SearchConstructor("","","","10-6","",false,false);
		assertFalse(s.valid());
		s = new SearchConstructor("","","","5-10","",false,false);
		assertTrue(s.valid());
		assertEquals("5-6",s.getLimit());
		
	}
	
	
	@Test
	public void testBuildLimitRange(){
		SearchConstructor s = new SearchConstructor("variables","","","1-10","csv",false,false);
		s.valid();
		String str = s.buildXquery();
		assertTrue(str.matches("(.)*, 1, 10\\)(.)*"));
		s = new SearchConstructor("variables","","","5-10","csv",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*, 5, 6\\)(.)*"));
	}
	
	@Test
	public void testBuildSortStatement(){
		SearchConstructor s = new SearchConstructor("variables","","variablename","1-10","csv",false,false);
		s.valid();
		String str = s.buildXquery();
		assertTrue(str.matches("(.)*order by  lower-case\\(\\$var/@name(.)*"));
		s = new SearchConstructor("variables","","id-","1-10","csv",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*order by  lower-case\\(\\$var/@ID\\) descending(.)*"));
		s = new SearchConstructor("variables","","productdate","1-10","csv",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*order by  lower-case\\(\\$var/../../docDscr/citation/prodStmt/prodDate/@date\\)(.)*"));
		 
	}
	@Test
	public void testBuildWhereStatement(){
		/*
		SearchConstructor s = new SearchConstructor("","","","","",false,false);
		s.valid();
		String str = s.buildXquery();
		assertFalse(str.matches("(.)*where(.)*"));
		s = new SearchConstructor("variables","ALLFIELDS=age","","","xml",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*where(.)*age(.)*"));
		s = new SearchConstructor("variables","variablename=age","","","xml",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*where  \\(\\(\\$var/@name contains text   \\(\\\"age\\\" using stemming\\)\\)\\)(.)*"));
		s = new SearchConstructor("variables","variablename=age|variablelabel!=date","","","xml",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*where  \\(\\(\\$var/@name contains text   \\(\\\"age\\\" using stemming\\)\\)\\) or not \\(\\(\\$var/labl contains text   \\(\\\"date\\\" using stemming\\)\\)\\)(.)*"));
		s = new SearchConstructor("variables","variablename=age|variablelabel!=date,variabletext=ancestry","","","xml",false,false);
		s.valid();
		str = s.buildXquery();
		assertTrue(str.matches("(.)*where  \\(\\(\\$var/@name contains text   \\(\\\"age\\\" using stemming\\)\\)\\) or not \\(\\(\\$var/labl contains text   \\(\\\"date\\\" using stemming\\)\\)\\) and \\(\\(\\$var/txt contains text   \\(\\\"ancestry\\\" using stemming\\)\\)\\)(.)*"));
		*/
	}
}