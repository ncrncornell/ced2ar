package edu.ncrn.cornell.ced2ar.search.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ncrn.cornell.ced2ar.web.classes.*;

/**
 *Tests the parsing of special characters into advanced searches
 */
public class AdvancedParseTest{

	@Test
	public void testClean(){
		String s = AdvancedParse.main("test!|@-#*$", "all");
		assertEquals("test | - *",s);
		s = AdvancedParse.main("|??&^*123asdf%%|$$$$", "all");
		assertEquals("| *123asdf |",s);	
		s = AdvancedParse.main("age&race", "all");
		assertEquals("age race",s);
	}
	
	@Test
	public void testParseAny(){
		String s = AdvancedParse.main("race age gender date ancestry", "any");
		assertEquals("race|age|gender|date|ancestry",s);
		s = AdvancedParse.main("race,age,gender,date,ancestry", "any");
		assertEquals("race|age|gender|date|ancestry",s);
		s = AdvancedParse.main("race, age, gender, date, ancestry", "any");
		assertEquals("race|age|gender|date|ancestry",s);
		s = AdvancedParse.main("race%%%%%%%%%%age", "any");
		assertEquals("race|age",s);
		s = AdvancedParse.main("race||||||||||||||age,", "any");
		assertEquals("race|age",s);
	}

	@Test
	public void testParseNone(){

		String s = AdvancedParse.main("race age gender date ancestry", "none");
		assertEquals("-race -age -gender -date -ancestry ",s);
		s = AdvancedParse.main("--------race", "none");
		assertEquals("-race ",s);
		s = AdvancedParse.main("age|race|gender|access", "none");
		assertEquals("-age|race|gender|access ",s);
		s = AdvancedParse.main("age,race)", "none");
		assertEquals("-age -race ",s);
		s = AdvancedParse.main("-age -race)", "none");
		assertEquals("-age -race ",s);
		s = AdvancedParse.main("age | race)", "none");
		assertEquals("-age | -race ",s);
	}
}