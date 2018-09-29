package edu.cornell.ncrn.ced2ar.web.classes;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import edu.cornell.ncrn.ced2ar.web.classes.*;

/**
 *Tests the parsing of special characters into advanced searches
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class AdvancedParseTest{

	@Test
	public void testClean(){
		String s = AdvancedParse.main("test!|@-#*$", "all");
		Assert.assertEquals("test | - *",s);
		s = AdvancedParse.main("|??&^*123asdf%%|$$$$", "all");
		Assert.assertEquals("| *123asdf |",s);
		s = AdvancedParse.main("age&race", "all");
		Assert.assertEquals("age race",s);
	}
	
	@Test
	public void testParseAny(){
		String s = AdvancedParse.main("race age gender date ancestry", "any");
		Assert.assertEquals("race|age|gender|date|ancestry",s);
		s = AdvancedParse.main("race,age,gender,date,ancestry", "any");
		Assert.assertEquals("race|age|gender|date|ancestry",s);
		s = AdvancedParse.main("race, age, gender, date, ancestry", "any");
		Assert.assertEquals("race|age|gender|date|ancestry",s);
		s = AdvancedParse.main("race%%%%%%%%%%age", "any");
		Assert.assertEquals("race|age",s);
		s = AdvancedParse.main("race||||||||||||||age,", "any");
		Assert.assertEquals("race|age",s);
	}

	@Test
	public void testParseNone(){

		String s = AdvancedParse.main("race age gender date ancestry", "none");
		Assert.assertEquals("-race -age -gender -date -ancestry ",s);
		s = AdvancedParse.main("--------race", "none");
		Assert.assertEquals("-race ",s);
		s = AdvancedParse.main("age|race|gender|access", "none");
		Assert.assertEquals("-age|race|gender|access ",s);
		s = AdvancedParse.main("age,race)", "none");
		Assert.assertEquals("-age -race ",s);
		s = AdvancedParse.main("-age -race)", "none");
		Assert.assertEquals("-age -race ",s);
		s = AdvancedParse.main("age | race)", "none");
		Assert.assertEquals("-age | -race ",s);
	}
}